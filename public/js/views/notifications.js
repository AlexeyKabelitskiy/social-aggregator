define(['require', 'jquery', 'backbonejs','underscorejs','../aggregator'
        ,'../models/notification-model'],
    function(require, $, Backbone, _, aggregator){
        aggregator.NotificationsView = Backbone.View.extend({
            initialize: function() {

                //Subscribe to new notifications
                aggregator.connection.onmessage('Notification', $.proxy(function(obj){
                    var n = new aggregator.Notification(obj);
                    this.collection.add(n, {at: 0});
                    this.$el.prepend(new aggregator.NotificationView({model: n, async:true}).render().el)
                }, this));
            },

            fetch: function() {
                var thisView = this;
                this.collection.reset([]);
                this.collection.fetch({
                    success: function() {
                        thisView.render();
                    },
                    error: function() {
                        console.log("Error fetching from "+thisView.collection.url)
                    }
                })
            },

            render: function() {
                this.$el.empty();
                var self = this;
                this.collection.each(function(notif){
                    self.$el.append(new aggregator.NotificationView({model: notif}).render().el);
                });
                return this;
            }



        });

        aggregator.NotificationView = Backbone.View.extend({
            render: function() {
                var icon = 'glyphicon-info-sign';
                var text = '';
                if(this.model.attributes.priority == 1) {
                    text = 'nf-debug'
                } else if(this.model.attributes.priority == 2) {
                    icon = 'glyphicon-ok-sign';
                }else if(this.model.attributes.priority == 3) {
                    icon = 'glyphicon-question-sign';
                    text = 'nf-warning'
                }else if(this.model.attributes.priority == 4) {
                    icon = 'glyphicon-exclamation-sign';
                    text = 'nf-error'
                }

                var arrived = "";
                if(this.async) {
                    arrived = " nf-highlight"
                }
                var model = _.extend({icon: icon, textDecoration: text+arrived}, this.model.attributes);
                this.$el.html(this.template(model));
                return this;
            }
        });
    }
);