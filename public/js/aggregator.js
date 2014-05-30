define(['jquery','underscorejs','backbonejs','bootstrap'], function($, _, Backbone) {

    var aggregator = {

        views: {},

        models: {},

        templates: {},

        loadTemplates: function (views, callback) {

            var deferreds = [];

            $.each(views, function (index, view) {
                var viewName = view+"View";
                    deferreds.push($.get('assets/tmpl/' + view + '.html', function (data) {
                        if (aggregator[viewName]) {
                            aggregator[viewName].prototype.template = _.template(data);
                        } else {
                            aggregator.templates[view] = _.template(data);
                        }
                    }, 'html'));
            });

            $.when.apply(null, deferreds).done(callback);
        },

        baseUrl: function() {
            var url = window.location.href.replace(window.location.hash, '');
            return url.replace(/\/*\/$/,'')+'/api'
        }

    };

    return aggregator;

});