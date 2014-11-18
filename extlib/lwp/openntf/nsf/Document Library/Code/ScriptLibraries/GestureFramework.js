window.GestureFramework = {
	handlers: {},
	
	on: function(target, eventName, callback) {
		var handler = this.handlers[eventName],
			self = this;
		if(!handler) {
			throw 'No handler for "' + eventName + '" exists.';
		}
		
		target = (typeof(target) !== "string") ? target : document.getElementById(target);
		
		target.addEventListener('touchstart', function(e) {
			e.GF = {
				eventName: eventName,
				eventType: 'touchstart'
			};
			callback.call(this, e, self);
		});
	},
	addHandler: function(eventName, handler) {
		this.handlers[eventName] = handler;
	}
};
