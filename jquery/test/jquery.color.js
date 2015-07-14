(function($) {
	$.fn.extend({
		"color": function(value) {
			if (value) {
				return this.css("color");
			} else {
				return this.css("color", value);
			}
		}
	})
}(jQuery))