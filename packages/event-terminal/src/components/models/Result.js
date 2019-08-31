import daggy from 'daggy'

export const Result = daggy.taggedSum('Result', {
	NotAsked: [],
	Pending: [],
	Ok: ['data'],
	Failure: ['error'],
})

Result.prototype.okOrNull = function() {
	return this.cata({
		NotAsked: () => null,
		Pending: () => null,
		Ok: data => data,
		Failure: () => null,
	})
}
