import { unionize, ofType, UnionOf } from 'unionize'

export const ApiRequest = unionize({
  NOT_ASKED: {},
  LOADING: {},
  OK: ofType<{ data: string }>(),
  NOT_OK: ofType<{ error: any }>()
})

export type RequestFromApi = UnionOf<typeof ApiRequest>
