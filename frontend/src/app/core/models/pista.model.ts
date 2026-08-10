export type Deporte = 'TENIS' | 'PADEL' | 'FUTBOL';

export interface PistaResponse {
  id: number;
  nombre: string;
  deporte: Deporte;
  activa: boolean;
}
