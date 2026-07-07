package com.reservas_gimnasio.proyecto.unit;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.reservas_gimnasio.proyecto.Schedulers.ReservaScheduler;
import com.reservas_gimnasio.proyecto.Services.ReservaService;

@ExtendWith(MockitoExtension.class)

public class ReservaSchedulerTest {

@Mock
private ReservaService reservaService;

@InjectMocks
private ReservaScheduler reservaScheduler;


@Test
void ejecutarMarcarReservasVencidasComoCompletadasDelegaEnElService() {
    // No probamos el disparo real del @Scheduled (fixedRate=900000 no es
    // práctico en un test); invocamos el método directamente y comprobamos
    // que delega en el Service, que es donde vive la lógica real.
    reservaScheduler.ejecutarMarcarReservasVencidasComoCompletadas();

    verify(reservaService).marcarReservasVencidasComoCompletadas();
}

}
