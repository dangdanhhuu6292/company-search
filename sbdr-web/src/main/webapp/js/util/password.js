// The length of the password has to be at least 8 characters long
function passLengthInvalid(targetForm, targetField, submitted) {
	if(!targetForm[targetField].$dirty && submitted) {
		return true;
	}
	if(!targetForm[targetField].$dirty) {
		return false;
	}

	var pass1 = targetForm[targetField].$modelValue;

	if(pass1 != null) {
		if(pass1.length >= 8) {
			return false;
		}
	}

	return true;
}

// The password may not contain spaces
function passSpacesInvalid(targetForm, targetField, submitted) {
	if(!targetForm[targetField].$dirty && submitted) {
		return true;
	}
	if(!targetForm[targetField].$dirty) {
		return false;
	}

	var pass1 = targetForm[targetField].$modelValue;

	var re = /^\s|\s/; // ^[ \s]+|[ \s]+$
	if(pass1 != null) {
		if(re.test(pass1)) {
			return true;
		}
	}

	return false;
}

// The password has to contain at least one small letter
function passLetterInvalid(targetForm, targetField, submitted) {
	if(!targetForm[targetField].$dirty && submitted) {
		return true;
	}
	if(!targetForm[targetField].$dirty) {
		return false;
	}

	var pass1 = targetForm[targetField].$modelValue;

	var re = /[a-z]/;
	if(pass1 != null) {
		if(re.test(pass1)) {
			return false;
		}
	}

	return true;
}

// The password has to contain at least one number
function passNumberInvalid(targetForm, targetField, submitted) {
	if(!targetForm[targetField].$dirty && submitted) {
		return true;
	}
	if(!targetForm[targetField].$dirty) {
		return false;
	}

	var pass1 = targetForm[targetField].$modelValue;

	var re = /[0-9]/;
	if(pass1 != null) {
		if(re.test(pass1)) {
			return false;
		}
	}

	return true;
}

// The password has to contain at least one capital letter
function passCapitalInvalid(targetForm, targetField, submitted) {
	if(!targetForm[targetField].$dirty && submitted) {
		return true;
	}
	if(!targetForm[targetField].$dirty) {
		return false;
	}

	var pass1 = targetForm[targetField].$modelValue;

	var re = /[A-Z]/;
	if(pass1 != null) {
		if(re.test(pass1)) {
			return false;
		}
	}

	return true;
}

var lengthString = " minimaal 7 karakters";
var letterString = " 1 kleine letter";
var numberString = " 1 nummer";
var capitalString = " 1 hoofdletter";
var spacesString = " geen spatie";
