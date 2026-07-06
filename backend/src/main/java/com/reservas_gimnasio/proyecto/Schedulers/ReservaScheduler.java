package com.reservas_gimnasio.proyecto.Schedulers;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.reservas_gimnasio.proyecto.Services.ReservaService;

@Component
public class ReservaScheduler {

	private final ReservaService reservaService;

	public ReservaScheduler(ReservaService reservaService) {
		this.reservaService = reservaService;
	}

	@Scheduled(fixedRate = 900000)
	public void ejecutarMarcarReservasVencidasComoCompletadas() {
		reservaService.marcarReservasVencidasComoCompletadas();
	}

}
