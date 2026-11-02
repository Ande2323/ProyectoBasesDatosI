const MONEDA = new Intl.NumberFormat('es-CO', { maximumFractionDigits: 0 });

export function precio(valor: number | null | undefined): string {
  return valor === null || valor === undefined ? 'Sin precio' : `$${MONEDA.format(valor)}`;
}

/** 2026-11-04 -> 04/11/2026 */
export function fecha(iso: string | null | undefined): string {
  if (!iso) {
    return '—';
  }
  const [anio, mes, dia] = iso.split('-');
  return `${dia}/${mes}/${anio}`;
}

/** Símbolos de {2}{U}{U} -> ['2', 'U', 'U'] */
export function simbolosDeMana(coste: string | null): string[] {
  return (coste ?? '').match(/\{[^}]+\}/g)?.map((s) => s.slice(1, -1)) ?? [];
}
