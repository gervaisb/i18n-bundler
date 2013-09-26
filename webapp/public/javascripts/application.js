/**
 * Hack who allow us to use Play! rendered template inside our scripts. This 
 * function expect a lines provider as parameter. The lines provider is a 
 * function with his body commented and containing the lines :
 *   var provider = function(){/*!
 *   	Here you can put
 *   	your 
 *   	multilines
 *   	content <star>/}
 * @param provider : A function with a commented body.
 * 
 * @see http://stackoverflow.com/questions/805107/creating-multiline-strings-in-javascript
 */
function text(provider) {
	return provider.toString()
		.replace(/^[^\/]+\/\*!?/, '')
		.replace(/\*\/[^\/]+$/, '');
}

function escape(string) {
	return string.replace(/[!"#$%&'()*+,.\/:;<=>?@\[\\\]^`{|}~]/g, '\\$&');
}

/**
 * Validate a form field with a constraints and update his visual state.
 * @param $field 	 The JQuery field to validate
 * @param constraint A function who receive the field value and must return true or false
 * @param message	 The message to display; When missing, the constraint name is used.
 * @returns {Boolean}
 */
function validate($field, constraint, message) {
	var _message = message===undefined?constraint.name:message;
		
	$field.closest('.control-group')
		.removeClass('error').find('.cause').remove();
	
	if ( !constraint($field.val()) ) {
		$field.closest('.control-group')
			.addClass('error').find('.help-inline').append('<span class="cause">'+_message+'</span>');
		return false;
	} else {
		return true;
	}
}
function notEmpty(value) {
	return value.trim().length>0;
}

function flash(message, target) {
	var expire = new Date(new Date().getTime()+(60000*10)),
		value  = 'flash-message='+escape(message)+'; expires='+expire.toUTCString()+'; path=/';	
	document.cookie = value;
	alert("COOKIE SET \n"+
		  "Expire  : "+expire+"\n"+
		  "Message : "+message+"\n"+
		  "\""+value+"\"");
}

function show() {
	alert("COOKIE READ : \n"+
		  "\""+document.cookie+"\"");
}


/** Add trim() for IE */
if(typeof String.prototype.trim !== 'function') {
	String.prototype.trim = function() {
		return this.replace(/^\s+|\s+$/g, ''); 
	}
}

/** Initialize the global application behaviors. */
$(function(){
	
	/**
	 * Use Ajax to load data-remote="true" links. 
	 */
	$('a[data-remote="true"]').click(function(){
		$.get($(this).attr('href'));
		return false;
	});
	
	/**
	 * When a .tabbale is present we select the one identified by the hash part
	 * of the request url. Or the first tab when no hash or no tab for the hash.
	 */ 
	var $tabbable = $('.tabbable');
	if ( $tabbable.length>0 ) {
		var $tab = $();
		if ( window.location.hash!==undefined && window.location.hash.replace('#','')!='') {
			$tab = $tabbable.find('.nav-tabs a[href="'+window.location.hash+'"]');
		} 
		if ( $tab.length<1 && $tabbable.find('.nav-tabs a.active').length==0 ) {
			$tab = $tabbable.find('.nav-tabs a[data-toggle="tab"]:first');
		}		
		$tab.tab('show');
	}
	
//	flash("Sample message");
//	show();
	
});
