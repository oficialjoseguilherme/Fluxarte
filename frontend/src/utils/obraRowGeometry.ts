export const OBRA_CARD_WIDTH = 250

export function circularPosition(position: number, length: number): number {
  return length > 0 ? ((position % length) + length) % length : 0
}

export function rowWindow(position: number, viewportWidth: number, itemCount: number) {
  const step = Math.round(viewportWidth / 2)
  const buffer = Math.ceil(step / OBRA_CARD_WIDTH) + 1
  const first = Math.floor(position / OBRA_CARD_WIDTH) - buffer
  const count = Math.ceil(viewportWidth / OBRA_CARD_WIDTH) + 1 + 2 * buffer
  return {
    step,
    offset: -buffer * OBRA_CARD_WIDTH - position % OBRA_CARD_WIDTH,
    slots: itemCount > 0 ? Array.from({ length: count }, (_, slot) => ({
      key: first + slot,
      index: circularPosition(first + slot, itemCount),
    })) : [],
  }
}
