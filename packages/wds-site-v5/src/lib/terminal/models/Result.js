import daggy from 'daggy';

export const Result = daggy.taggedSum('Result', {
	None: [],
	Pending: [],
	Ok: ['data'],
	Failure: ['error']
});

Result.prototype.okOrNull = function () {
	return this.cata({
		None: () => null,
		Pending: () => null,
		Ok: (data) => data,
		Failure: () => null
	});
};
