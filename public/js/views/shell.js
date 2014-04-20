define(['require', 'jquery', 'backbonejs','underscorejs','../aggregator'], function(require, $, Backbone, _, aggregator){
    aggregator.ShellView = Backbone.View.extend({

        render: function() {
            this.$el.html(this.template({}));
        },

        selectMenuItem: function(menuItem) {
            $('.navbar .nav li').removeClass('active');
            if (menuItem) {
                $('.' + menuItem).addClass('active');
            }
        }

    });
});