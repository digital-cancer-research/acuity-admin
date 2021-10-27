utilsModule = {};

utilsModule.handleRegexValidation = function (event, element) {
    var regex = new RegExp('^([a-zA-Z0-9_ ,-])+$');
    var key = event.charCode;
    var string = key ? String.fromCharCode(key) : event.clipboardData.getData('text');
    if (!regex.test(string)) {
        var elem = element.css("background-color", "red");
        window.setTimeout(function () {
          elem.css("background-color", "white");
        }, 300);
        return false;
    }
};


utilsModule.split = (function ($, undefined) {
    var PANEL_LEFT = 'left_panel';
    var PANEL_RIGHT = 'right_panel';
    var count = 0;
    var splitterId = null;
    var splitters = [];
    var currentSplitter = null;
    $.fn.split = function (options) {
        var settings = $.extend({
            limit: 100,
            position: '50%',
            onDragStart: $.noop,
            onDragEnd: $.noop,
            onDrag: $.noop
        }, options || {});

        var lastPosition = settings.position;
        var children = this.children();
        var panelLeft = children.first().addClass(PANEL_LEFT);
        var panelRight = panelLeft.next().addClass(PANEL_RIGHT);
        var splitterKind = 'vspliter';
        var width = this.width();
        this.lastPercentPos = lastPosition / width;
        this.onDrag = settings.onDrag;
        var id = count++;
        this.addClass('spliter_panel');
        var spliter = $('<div/>').addClass(splitterKind).mouseenter(function () {
            splitterId = id;
        }).mouseleave(function () {
            splitterId = null;
        }).insertAfter(panelLeft);
        var self = $.extend(this, {
            position: function (n) {
                if (n === undefined) {
                    return undefined;
                } else {
                    var sw = spliter.width() / 2;
                    spliter.css('left', n - sw);
                    panelLeft.width(n - sw);
                    panelRight.width(self.width() - n - sw);
                }
            },
            limit: settings.limit,
            isActive: function () {
                return splitterId === id;
            },
            destroy: function () {
                spliter.unbind('mouseenter');
                spliter.unbind('mouseleave');
                panelLeft.removeClass(PANEL_LEFT);
                panelRight.removeClass(PANEL_RIGHT);
                self.unbind('spliter.resize');
                splitters[id] = null;
                spliter.remove();
                for (var i = splitters.length; i--;) {
                    if (splitters[i] !== null) {
                        $(document.documentElement).unbind('.spliter');
                        splitters = [];
                        break;
                    }
                }
                self = null;
            }
        });

        $(window).bind('resize', function (w) {
            for (var i = 0; i < splitters.length; i++) {
                if (splitters[i]) {
                    splitters[i].position(splitters[i].width() * splitters[i].lastPercentPos);
                }
            }
        });

        self.bind('spliter.resize', function () {
            var pos = self.position();
            if (pos > self.width()) {
                pos = self.width() - self.limit - 1;
            }
            if (pos < self.limit) {
                pos = self.limit + 1;
            }
            self.position(pos);
        });
        var matches = settings.position.match(/^([0-9]+)(%)?$/);
        var pos;
        if (matches[2]) {
            pos = (width * +matches[1]) / 100;
        } else {
            pos = settings.position;
        }
        if (pos > width - settings.limit) {
            pos = width - settings.limit;
        }
        if (pos < settings.limit) {
            pos = settings.limit;
        }
        self.position(pos);
        if (splitters.length === 0) {
            $(document.documentElement).bind('mousedown.spliter', function (e) {
                if (splitterId !== null) {
                    currentSplitter = splitters[splitterId];
                    $('<div class="splitterMask"></div>').insertAfter(currentSplitter);
                    $('body').css('cursor', 'e-resize');
                    settings.onDragStart(e);
                    return false;
                }
            }).bind('mouseup.spliter', function (e) {
                if (currentSplitter != null) {
                    var newPosition = currentSplitter.position();
                    if (newPosition !== lastPosition) {
                        lastPosition = newPosition;
                        currentSplitter.lastPercentPos = newPosition / currentSplitter.width();
                    }
                }
                currentSplitter = null;
                splitterId = null;
                $('div.splitterMask').remove();
                $('body').css('cursor', 'auto');
                settings.onDragEnd(e);
            }).bind('mousemove.spliter', function (e) {
                if (currentSplitter !== null) {
                    var limit = currentSplitter.limit;
                    var x = e.pageX - currentSplitter.offset().left;
                    if (x <= limit) {
                        x = limit + 1;
                    }
                    else if (x >= currentSplitter.width() - limit) {
                        x = currentSplitter.width() - limit - 1;
                    }
                    if (x > limit &&
                        x < currentSplitter.width() - limit) {
                        currentSplitter.position(x);
                        currentSplitter.find('.spliter_panel').trigger('spliter.resize');
                        currentSplitter.onDrag(e);
                        return false;
                    }
                    currentSplitter.onDrag(e);
                }
            });
        }
        splitters.push(self);
        return self;
    };
})(jQuery);