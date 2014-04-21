define(['jquery','underscorejs','backbonejs','bootstrap'], function($, _, Backbone) {

    var aggregator = {

        views: {},

        models: {},

        loadTemplates: function (views, callback) {

            var deferreds = [];

            $.each(views, function (index, view) {
                var viewName = view+"View";
                if (aggregator[viewName]) {
                    deferreds.push($.get('assets/tmpl/' + view + '.html', function (data) {
                        aggregator[viewName].prototype.template = _.template(data);
                    }, 'html'));
                } else {
                    alert(viewName + " not found");
                }
            });

            $.when.apply(null, deferreds).done(callback);
        },

        baseUrl: function() {
            var url = window.location.href.replace(window.location.hash, '');
            return url.replace(/\/*\/$/,'')
        }

    };

    return aggregator;

});