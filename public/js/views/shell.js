define(['require', 'jquery', 'backbonejs','underscorejs','../aggregator',
        'jquery-blockui'
       ],
    function(require, $, Backbone, _, aggregator){
    aggregator.ShellView = Backbone.View.extend({

        $errorBar : $("#errorBar"),

        initialize: function () {
            var self = this;
            this.blockPageEvents = {
                request: function(collection, xhr, options) {
                    $.blockUI({message: "Loading..."});
                },
                sync: function(collection, resp, options) {
                    $.unblockUI();
                },
                error: function(collection, resp, options) {
                    $.unblockUI();
                    self.addError("Error loading data. "+resp.status+": "+resp.statusText);
                }
            }
        },

        content : function() {
           if(!this.$content || this.$content.length == 0) {
               this.$content = $("#content")
           }
            return this.$content;
        },

        render: function() {
            this.$el.html(this.template({}));
            return this;
        },

        selectMenuItem: function(menuItem) {
            $('.navbar .nav li').removeClass('active');
            if (menuItem) {
                $('#' + menuItem).addClass('active');
            }
        },

        addError: function (message) {
            var newAlert = aggregator.templates.Alert({message: message});
            this.$errorBar.prepend(newAlert);
        }

    });
});