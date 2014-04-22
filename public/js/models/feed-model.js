define(['require', 'jquery', 'backbonejs', 'underscorejs', '../aggregator', 'moment'], function (require, $, Backbone, _, aggregator, moment) {
    aggregator.FeedItem = Backbone.Model.extend({

        initialize: function () {
            var date = this.formatRelativeDate(this.attributes.time);
            this.attributes.timestamp = date;
        },

        getDate: function () {
            if (this.attributes.time) {
                return new Date(this.attributes.time)
            }
            return null;
        },

        formatRelativeDate: function(date) {
            moment.lang('en', {
                calendar: {
                    lastDay: '[Yesterday at] LT',
                    sameDay: 'LT',
                    nextDay: '[Tomorrow at] LT',
                    lastWeek: '[last] dddd [at] LT',
                    nextWeek: 'dddd [at] LT',
                    sameElse: 'L'
                }
            });
            return moment(date).calendar();
        }
    });

    aggregator.Feed = Backbone.Collection.extend({
        model: aggregator.FeedItem,
        url: aggregator.baseUrl() + '/feed'
    });
});