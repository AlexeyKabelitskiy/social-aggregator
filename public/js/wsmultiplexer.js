define([], function() {

    function Multiplexer(url) {
        var self = this;
        this.subscriptions = {
            message : {},
            open: [],
            close: [],
            error: []
        };

        this.connection = new WebSocket(url);
        this.connection.onopen = function(event) {
           console.info("Websocket started. "+event);
           self.subscriptions.open.forEach(function(callback){
               callback.apply(self, event)
           })
        };
        this.connection.onerror = function(event) {
            console.info("Websocket error. "+event);
            self.subscriptions.error.forEach(function(callback){
                callback.apply(self, event)
            })
        };
        this.connection.onclose = function(code, reason, wasClean) {
            console.info("Websocket closed. Code "+code+", reason '"+reason+"', clean "+wasClean);
            self.subscriptions.close.forEach(function(callback){
                callback.apply(self, code, reason, wasClean);
            })
        };
        this.connection.onmessage = function(msg) {
            var obj = JSON.parse(msg.data);
            var callback = self.subscriptions.message[obj.eventType];
            if( "function" === typeof callback){
                callback.call(this, obj.payload);
            }
        };

    }

    Multiplexer.prototype.onmessage = function(eventType, callback) {
        if(eventType)
            this.subscriptions.message[eventType] = callback;
        else
            console.warn("Unknown message event type: "+eventType)
    };
    Multiplexer.prototype.on = function(type, callback) {
        //Protect against message type, this should be another type of subscription
        if(type !== message) {
            var arr = this.subscriptions[type] || [];
            arr.push(callback);
            this.subscriptions[type] = arr;
        }
    };

    return Multiplexer;

});
