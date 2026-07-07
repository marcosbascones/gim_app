package com.reservas_gimnasio.proyecto.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.reservas_gimnasio.proyecto.Dto.Reserva.ReservaRequestDTO;
import com.reservas_gimnasio.proyecto.Dto.Reserva.ReservaResponseDTO;
import com.reservas_gimnasio.proyecto.Exceptions.ReglaNegocioException;
import com.reservas_gimnasio.proyecto.Repositories.BloqueoRepository;
import com.reservas_gimnasio.proyecto.Repositories.PistaRepository;
import com.reservas_gimnasio.proyecto.Repositories.ReservaRepository;
import com.reservas_gimnasio.proyecto.Repositories.UsuarioRepository;
import com.reservas_gimnasio.proyecto.Services.ReservaService;
import com.reservas_gimnasio.proyecto.models.Bloqueo;
import com.reservas_gimnasio.proyecto.models.Pista;
import com.reservas_gimnasio.proyecto.models.Reserva;
import com.reservas_gimnasio.proyecto.models.Usuario;

@ExtendWith(MockitoExtension.class)

public class ReservaServiceTest {

@Mock
private ReservaRepository reservaRepository;

@Mock
private UsuarioRepository usuarioRepository;

@Mock
private PistaRepository pistaRepository;

@Mock
private BloqueoRepository bloqueoRepository;

@InjectMocks
private ReservaService reservaService;


// ---------- crearReserva ----------

@Test
void crearReservaFechaInicioEnElPasadoLanzaExcepcion() {
    ReservaRequestDTO request = new ReservaRequestDTO(
        1L, 1L,
        LocalDateTime.now().minusHours(1),
        LocalDateTime.now().plusHours(1)
    );

    assertThrows(ReglaNegocioException.class, () -> reservaService.crearReserva(request));
}

@Test
void crearReservaDuracionMenorAUnaHoraLanzaExcepcion() {
    LocalDateTime inicio = LocalDateTime.now().plusDays(1);

    ReservaRequestDTO request = new ReservaRequestDTO(1L, 1L, inicio, inicio.plusMinutes(30));

    assertThrows(ReglaNegocioException.class, () -> reservaService.crearReserva(request));
}

@Test
void crearReservaDuracionMayorADosHorasLanzaExcepcion() {
    LocalDateTime inicio = LocalDateTime.now().plusDays(1);

    ReservaRequestDTO request = new ReservaRequestDTO(1L, 1L, inicio, inicio.plusHours(3));

    assertThrows(ReglaNegocioException.class, () -> reservaService.crearReserva(request));
}

@Test
void crearReservaUsuarioConTresReservasActivasLanzaExcepcion() {
    LocalDateTime inicio = LocalDateTime.now().plusDays(1);
    ReservaRequestDTO request = new ReservaRequestDTO(1L, 1L, inicio, inicio.plusHours(1));

    Usuario usuario = new Usuario();
    usuario.setId(1L);

    when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
    when(reservaRepository.countByUsuarioAndEstadoAndFechaHoraInicioAfter(
        any(), any(), any()
    )).thenReturn(3L);

    assertThrows(ReglaNegocioException.class, () -> reservaService.crearReserva(request));
}

@Test
void crearReservaUsuarioConDosReservasActivasSeCreaSinProblema() {
    LocalDateTime inicio = LocalDateTime.now().plusDays(1);
    ReservaRequestDTO request = new ReservaRequestDTO(1L, 1L, inicio, inicio.plusHours(1));

    Usuario usuario = new Usuario();
    usuario.setId(1L);
    usuario.setNombre("Ana");

    Pista pista = new Pista();
    pista.setId(1L);
    pista.setNombre("Pista Tenis 1");

    Reserva guardada = new Reserva();
    guardada.setId(10L);
    guardada.setUsuario(usuario);
    guardada.setPista(pista);
    guardada.setFechaHoraInicio(request.getFechaHoraInicio());
    guardada.setFechaHoraFin(request.getFechaHoraFin());
    guardada.setEstado(Reserva.EstadoReserva.CONFIRMED);

    when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
    when(reservaRepository.countByUsuarioAndEstadoAndFechaHoraInicioAfter(
        any(), any(), any()
    )).thenReturn(2L);
    when(pistaRepository.findById(1L)).thenReturn(Optional.of(pista));
    when(reservaRepository.findByPistaAndFechaHoraInicioBeforeAndFechaHoraFinAfter(
        any(), any(), any()
    )).thenReturn(List.of());
    when(bloqueoRepository.findByPistaAndFechaHoraInicioBeforeAndFechaHoraFinAfter(
        any(), any(), any()
    )).thenReturn(List.of());
    when(reservaRepository.save(any(Reserva.class))).thenReturn(guardada);

    ReservaResponseDTO resultado = reservaService.crearReserva(request);

    assertEquals(Reserva.EstadoReserva.CONFIRMED, resultado.getEstado());
}

@Test
void crearReservaSolapamientoConReservaNoCanceladaLanzaExcepcion() {
    LocalDateTime inicio = LocalDateTime.now().plusDays(1);
    ReservaRequestDTO request = new ReservaRequestDTO(1L, 1L, inicio, inicio.plusHours(1));

    Usuario usuario = new Usuario();
    usuario.setId(1L);

    Pista pista = new Pista();
    pista.setId(1L);

    Reserva reservaExistente = new Reserva();
    reservaExistente.setEstado(Reserva.EstadoReserva.CONFIRMED);

    when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
    when(reservaRepository.countByUsuarioAndEstadoAndFechaHoraInicioAfter(
        any(), any(), any()
    )).thenReturn(0L);
    when(pistaRepository.findById(1L)).thenReturn(Optional.of(pista));
    when(reservaRepository.findByPistaAndFechaHoraInicioBeforeAndFechaHoraFinAfter(
        any(), any(), any()
    )).thenReturn(List.of(reservaExistente));

    assertThrows(ReglaNegocioException.class, () -> reservaService.crearReserva(request));
}

@Test
void crearReservaSolapamientoSoloConReservaCanceladaNoBloquea() {
    LocalDateTime inicio = LocalDateTime.now().plusDays(1);
    ReservaRequestDTO request = new ReservaRequestDTO(1L, 1L, inicio, inicio.plusHours(1));

    Usuario usuario = new Usuario();
    usuario.setId(1L);
    usuario.setNombre("Ana");

    Pista pista = new Pista();
    pista.setId(1L);
    pista.setNombre("Pista Tenis 1");

    Reserva reservaCancelada = new Reserva();
    reservaCancelada.setEstado(Reserva.EstadoReserva.CANCELLED);

    Reserva guardada = new Reserva();
    guardada.setId(10L);
    guardada.setUsuario(usuario);
    guardada.setPista(pista);
    guardada.setFechaHoraInicio(request.getFechaHoraInicio());
    guardada.setFechaHoraFin(request.getFechaHoraFin());
    guardada.setEstado(Reserva.EstadoReserva.CONFIRMED);

    when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
    when(reservaRepository.countByUsuarioAndEstadoAndFechaHoraInicioAfter(
        any(), any(), any()
    )).thenReturn(0L);
    when(pistaRepository.findById(1L)).thenReturn(Optional.of(pista));
    when(reservaRepository.findByPistaAndFechaHoraInicioBeforeAndFechaHoraFinAfter(
        any(), any(), any()
    )).thenReturn(List.of(reservaCancelada));
    when(bloqueoRepository.findByPistaAndFechaHoraInicioBeforeAndFechaHoraFinAfter(
        any(), any(), any()
    )).thenReturn(List.of());
    when(reservaRepository.save(any(Reserva.class))).thenReturn(guardada);

    ReservaResponseDTO resultado = reservaService.crearReserva(request);

    assertEquals(Reserva.EstadoReserva.CONFIRMED, resultado.getEstado());
}

@Test
void crearReservaSolapamientoConBloqueoLanzaExcepcion() {
    LocalDateTime inicio = LocalDateTime.now().plusDays(1);
    ReservaRequestDTO request = new ReservaRequestDTO(1L, 1L, inicio, inicio.plusHours(1));

    Usuario usuario = new Usuario();
    usuario.setId(1L);

    Pista pista = new Pista();
    pista.setId(1L);

    when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
    when(reservaRepository.countByUsuarioAndEstadoAndFechaHoraInicioAfter(
        any(), any(), any()
    )).thenReturn(0L);
    when(pistaRepository.findById(1L)).thenReturn(Optional.of(pista));
    when(reservaRepository.findByPistaAndFechaHoraInicioBeforeAndFechaHoraFinAfter(
        any(), any(), any()
    )).thenReturn(List.of());
    when(bloqueoRepository.findByPistaAndFechaHoraInicioBeforeAndFechaHoraFinAfter(
        any(), any(), any()
    )).thenReturn(List.of(new Bloqueo()));

    assertThrows(ReglaNegocioException.class, () -> reservaService.crearReserva(request));
}

@Test
void crearReservaCasoFelizGuardaConEstadoConfirmedYDatosCorrectos() {
    LocalDateTime inicio = LocalDateTime.now().plusDays(1);
    ReservaRequestDTO request = new ReservaRequestDTO(1L, 1L, inicio, inicio.plusHours(1));

    Usuario usuario = new Usuario();
    usuario.setId(1L);
    usuario.setNombre("Ana");

    Pista pista = new Pista();
    pista.setId(1L);
    pista.setNombre("Pista Tenis 1");

    Reserva guardada = new Reserva();
    guardada.setId(10L);
    guardada.setUsuario(usuario);
    guardada.setPista(pista);
    guardada.setFechaHoraInicio(request.getFechaHoraInicio());
    guardada.setFechaHoraFin(request.getFechaHoraFin());
    guardada.setEstado(Reserva.EstadoReserva.CONFIRMED);

    when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
    when(reservaRepository.countByUsuarioAndEstadoAndFechaHoraInicioAfter(
        any(), any(), any()
    )).thenReturn(0L);
    when(pistaRepository.findById(1L)).thenReturn(Optional.of(pista));
    when(reservaRepository.findByPistaAndFechaHoraInicioBeforeAndFechaHoraFinAfter(
        any(), any(), any()
    )).thenReturn(List.of());
    when(bloqueoRepository.findByPistaAndFechaHoraInicioBeforeAndFechaHoraFinAfter(
        any(), any(), any()
    )).thenReturn(List.of());
    when(reservaRepository.save(any(Reserva.class))).thenReturn(guardada);

    ReservaResponseDTO resultado = reservaService.crearReserva(request);

    assertEquals(10L, resultado.getId());
    assertEquals(1L, resultado.getUsuarioId());
    assertEquals("Ana", resultado.getUsuarioNombre());
    assertEquals(1L, resultado.getPistaId());
    assertEquals("Pista Tenis 1", resultado.getPistaNombre());
    assertEquals(Reserva.EstadoReserva.CONFIRMED, resultado.getEstado());
}


// ---------- cancelarReserva ----------

@Test
void cancelarReservaNoEncontradaLanzaExcepcion() {
    when(reservaRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> reservaService.cancelarReserva(1L, 1L));
}

@Test
void cancelarReservaNoConfirmedLanzaExcepcion() {
    Reserva reserva = new Reserva();
    reserva.setId(1L);
    reserva.setEstado(Reserva.EstadoReserva.CANCELLED);

    when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

    assertThrows(ReglaNegocioException.class, () -> reservaService.cancelarReserva(1L, 1L));
}

@Test
void cancelarReservaUsuarioNoEsDuenioNiAdminLanzaExcepcion() {
    Usuario dueño = new Usuario();
    dueño.setId(1L);

    Usuario otroUsuario = new Usuario();
    otroUsuario.setId(2L);
    otroUsuario.setRol(Usuario.Rol.USER);

    Reserva reserva = new Reserva();
    reserva.setId(1L);
    reserva.setEstado(Reserva.EstadoReserva.CONFIRMED);
    reserva.setUsuario(dueño);
    reserva.setFechaHoraInicio(LocalDateTime.now().plusDays(1));

    when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
    when(usuarioRepository.findById(2L)).thenReturn(Optional.of(otroUsuario));

    assertThrows(ReglaNegocioException.class, () -> reservaService.cancelarReserva(1L, 2L));
}

@Test
void cancelarReservaDuenioMenosDeDosHorasAntelacionLanzaExcepcion() {
    Usuario dueño = new Usuario();
    dueño.setId(1L);
    dueño.setRol(Usuario.Rol.USER);

    Reserva reserva = new Reserva();
    reserva.setId(1L);
    reserva.setEstado(Reserva.EstadoReserva.CONFIRMED);
    reserva.setUsuario(dueño);
    reserva.setFechaHoraInicio(LocalDateTime.now().plusMinutes(30));

    when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
    when(usuarioRepository.findById(1L)).thenReturn(Optional.of(dueño));

    assertThrows(ReglaNegocioException.class, () -> reservaService.cancelarReserva(1L, 1L));
}

@Test
void cancelarReservaDuenioMasDeDosHorasAntelacionSeCancelaCorrectamente() {
    Usuario dueño = new Usuario();
    dueño.setId(1L);
    dueño.setNombre("Ana");
    dueño.setRol(Usuario.Rol.USER);

    Pista pista = new Pista();
    pista.setId(1L);
    pista.setNombre("Pista Tenis 1");

    Reserva reserva = new Reserva();
    reserva.setId(1L);
    reserva.setEstado(Reserva.EstadoReserva.CONFIRMED);
    reserva.setUsuario(dueño);
    reserva.setPista(pista);
    reserva.setFechaHoraInicio(LocalDateTime.now().plusHours(3));
    reserva.setFechaHoraFin(LocalDateTime.now().plusHours(4));

    when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
    when(usuarioRepository.findById(1L)).thenReturn(Optional.of(dueño));
    when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

    ReservaResponseDTO resultado = reservaService.cancelarReserva(1L, 1L);

    assertEquals(Reserva.EstadoReserva.CANCELLED, resultado.getEstado());
}

@Test
void cancelarReservaAdminMenosDeDosHorasAntelacionSeCancelaIgualmente() {
    Usuario dueño = new Usuario();
    dueño.setId(1L);

    Usuario admin = new Usuario();
    admin.setId(2L);
    admin.setNombre("Admin");
    admin.setRol(Usuario.Rol.ADMIN);

    Pista pista = new Pista();
    pista.setId(1L);
    pista.setNombre("Pista Tenis 1");

    Reserva reserva = new Reserva();
    reserva.setId(1L);
    reserva.setEstado(Reserva.EstadoReserva.CONFIRMED);
    reserva.setUsuario(dueño);
    reserva.setPista(pista);
    reserva.setFechaHoraInicio(LocalDateTime.now().plusMinutes(30));
    reserva.setFechaHoraFin(LocalDateTime.now().plusMinutes(90));

    when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
    when(usuarioRepository.findById(2L)).thenReturn(Optional.of(admin));
    when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

    ReservaResponseDTO resultado = reservaService.cancelarReserva(1L, 2L);

    assertEquals(Reserva.EstadoReserva.CANCELLED, resultado.getEstado());
}

@Test
void cancelarReservaOrdenValidacionesR5AntesQueR7() {
    // Reserva ya CANCELLED (falla R5) Y solicitante que no es ni dueño ni admin
    // (fallaría también R7). Debe lanzar por R5 sin necesidad de consultar el
    // usuario solicitante, confirmando que la comprobación de estado va primero.
    Reserva reserva = new Reserva();
    reserva.setId(1L);
    reserva.setEstado(Reserva.EstadoReserva.CANCELLED);

    when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

    assertThrows(ReglaNegocioException.class, () -> reservaService.cancelarReserva(1L, 99L));
}


// ---------- marcarReservasVencidasComoCompletadas ----------

@SuppressWarnings("unchecked")
@Test
void marcarReservasVencidasComoCompletadasActualizaLasQueDevuelveElRepositorio() {
    Reserva vencida1 = new Reserva();
    vencida1.setId(1L);
    vencida1.setEstado(Reserva.EstadoReserva.CONFIRMED);

    Reserva vencida2 = new Reserva();
    vencida2.setId(2L);
    vencida2.setEstado(Reserva.EstadoReserva.CONFIRMED);

    // El propio repositorio ya filtra por CONFIRMED + fechaHoraFin pasada (probado
    // en ReservaRepositoryTest); aquí solo simulamos ese resultado exacto, sin
    // any() indiscriminado: el estado se fija con eq() y solo el instante de
    // "ahora" usa any(LocalDateTime.class), porque el Service lo calcula
    // internamente y no es predecible desde el test.
    when(reservaRepository.findByEstadoAndFechaHoraFinBefore(
        eq(Reserva.EstadoReserva.CONFIRMED), any(LocalDateTime.class)
    )).thenReturn(List.of(vencida1, vencida2));

    reservaService.marcarReservasVencidasComoCompletadas();

    ArgumentCaptor<List<Reserva>> captor = ArgumentCaptor.forClass(List.class);
    verify(reservaRepository).saveAll(captor.capture());

    List<Reserva> guardadas = captor.getValue();

    assertEquals(2, guardadas.size());
    assertTrue(guardadas.stream().allMatch(r -> r.getEstado() == Reserva.EstadoReserva.COMPLETED));
}

@Test
void marcarReservasVencidasComoCompletadasSinReservasVencidasGuardaListaVacia() {
    when(reservaRepository.findByEstadoAndFechaHoraFinBefore(
        eq(Reserva.EstadoReserva.CONFIRMED), any(LocalDateTime.class)
    )).thenReturn(List.of());

    reservaService.marcarReservasVencidasComoCompletadas();

    verify(reservaRepository).saveAll(List.of());
}

}
