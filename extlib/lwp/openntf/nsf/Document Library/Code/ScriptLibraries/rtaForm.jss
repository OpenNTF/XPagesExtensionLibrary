function isDocSubmitted() {
	var document = newResponseDoc.getDocument();
	
	return (document.getItemValue('SUBMITTED') === '1');
}

function isParentDoc() {
	if(typeof(newResponseDoc) === "undefined" || newResponseDoc.isResponse()) {
		return true;
	}
	return false;
}

function isReviewsReadOnly() {
	if(typeof(newResponseDoc) === "undefined") {
		return true;
	} else if(newResponseDoc.getDocument().getItemValueString('SUBMITTED') === DocLib.Review.Submitted && !viewScope.clearReviewCycle) {
		return true;
	}
	return false;
}

function timeLimit(value) {
	var target = getComponent('timeLimitDays');
	if(value === '1' || value === '2') {
		target.setRendered(true);
	} else {
		target.setRendered(false);
	}
}

function isReviewerOriginator() {
	var values = getComponent('reviewers').getValue(),
		originatorName = peopleBean.getPerson(@UserName()).displayName,
		i;
	
	if(values === null) {
		return false;
	}
	
	if(typeof(values) === "string" && values.length > 0) {
		return (peopleBean.getPerson(values).displayName === originatorName);
	} else {
		for(i = 0; i < values.length; i++) {
			if(peopleBean.getPerson(values[i]).displayName === originatorName) {
				return true;
			}
		}
	}
	return false;
}