define(['require', 'jquery', 'backbonejs','underscorejs','../aggregator'
        ,'../models/notification-model', 'jquery-color'],
    function(require, $, Backbone, _, aggregator){
        aggregator.NotificationsView = Backbone.View.extend({

            selectors : {
                menuItem:       'menu-notifications',
                menuIcon:       'new-notification',
                menuIconHash:   '#new-notification'
            },

            fetched: false,

            initialize: function(options) {

                options.hub.on("select", $.proxy(this.onselect, this));

                //Subscribe to new notifications
                aggregator.connection.onmessage('Notification', $.proxy(function(obj){
                    obj = obj || {};
                    obj.async = true;
                    var n = new aggregator.Notification(obj);
                    this.collection.add(n, {at: 0});
                    this.$el.prepend(new aggregator.NotificationView({model: n}).render().el);

                    if(this.selected === true) {
                        this.animateNewNotifications();
                    } else {
                        $(this.selectors.menuIconHash).addClass("glyphicon-exclamation-sign");
                    }

                }, this));

            },

            fetch: function() {
                var thisView = this;
                if(this.fetched !== true) {
                    this.collection.reset([]);
                    this.collection.fetch({
                        success: function () {
                            thisView.render();
                            thisView.fetched = true;
                        },
                        error: function () {
                            console.log("Error fetching from " + thisView.collection.url)
                        }
                    })
                }
            },

            render: function() {
                this.$el.empty();
                var self = this;
                this.collection.each(function(notif){
                    self.$el.append(new aggregator.NotificationView({model: notif}).render().el);
                });
                return this;
            },

            onselect: function(item) {
                if(item === this.selectors.menuItem) {
                    this.selected = true;
                    $(this.selectors.menuIconHash).removeClass("glyphicon-exclamation-sign");
                    this.animateNewNotifications();
                } else {
                    this.selected = false;
                }
            },

            animateNewNotifications: function () {
                $(".nf-highlight").animate({backgroundColor: "#ffffff"}, 1000, function(){
                    $(this).removeClass("nf-highlight")
                })
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
                if(this.model.attributes.async) {
                    arrived = " nf-highlight"
                }
                var model = _.extend({icon: icon, textDecoration: text+arrived}, this.model.attributes);
                this.$el.html(this.template(model));
                return this;
            }
        });
    }
);