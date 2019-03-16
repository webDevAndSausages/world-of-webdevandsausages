import { unionize, ofType, UnionOf } from 'unionize'

export const ApiRequest = unionize({
  NOT_ASKED: {},
  LOADING: {},
  OK: ofType<{ data: any }>(),
  NOT_OK: ofType<{ error: any; status: number }>()
})

export type RequestFromApi = UnionOf<typeof ApiRequest>
