/**
 * 判断 2 个数据是否相等（拥有同的元素个数及元素值）
 * @param array1 数组一
 * @param array2 数组二
 * @return 判断结果
 */
$.arrayEqual = function (array1, array2) {
    if (!$.isArray(array1) || !$.isArray(array2)) {
        return false;
    }

    if (array1.length != array2.length) {
        return false;
    }
    var isEquals = true;
    $.each(array1, function (i, n) {
        if ($.inArray(n, array2) < 0) {
            isEquals = false;
            return false;
        }
    });
    return isEquals;
};

/**
 * 求2个数组的并集
 * @param a 数组 1
 * @param b 数组 2
 * @returns 并集
 */
$.arrayUnion = function(a, b) {
    return $.merge(a, $.grep(b, function(i) {
        return $.inArray(i, a) == -1;
    }));
};

/**
 * 求2个数组的交集
 * @param a 数组 1
 * @param b 数组 2
 * @returns 交集
 */
$.arrayIntersect = function (a, b) {
    return $.merge($.grep(a, function (i) {
            return $.inArray(i, b) != -1;
        }), $.grep(b, function (i) {
            return $.inArray(i, a) != -1;
        })
    );
};

/**
 * 加载 css 样式
 * @param path url
 * @param callback 回调函数
 * @return {*}
 */
$.loadCss = function (path, callback) {
    if (!path) {
        return;
    }
    var l;
    if (!window["_loadCss"] || window["_loadCss"].indexOf(path) < 0) {
        l = document.createElement('link');
        l.setAttribute('type', 'text/css');
        l.setAttribute('rel', 'stylesheet');
        l.setAttribute('href', path);
        l.setAttribute("id", "loadCss" + Math.random());
        document.getElementsByTagName("head")[0].appendChild(l);
        window["_loadCss"] ? (window["_loadCss"] += "|" + path) : (window["_loadCss"] = "|" + path);
    }
    l && (typeof callback == "function") && (l.onload = callback);
    return true;
};

/**
 * 获取 cookie 中的数据
 * @param name cookie 名称
 * @return 数据
 */
$.getCookie = function (name) {
    var reg = new RegExp("(^| )" + name + "(?:=([^;]*))?(;|$)"), val = document.cookie.match(reg);
    return val ? (val[2] ? unescape(val[2]) : "") : null;
};

/**
 * 设置 cookie
 * @param name cookie 名称
 * @param value cookie 值
 * @param expires 过期时间
 * @param path 路径
 * @param domain 域名
 * @param secure 安全性
 */
$.setCookie = function (name, value, expires, path, domain, secure) {
    var exp = new Date(), expires = arguments[2] || null, path = arguments[3] || "/", domain = arguments[4] || null, secure = arguments[5] || false;
    expires ? exp.setMinutes(exp.getMinutes() + parseInt(expires)) : "";
    document.cookie = name + '=' + value + (expires ? ';expires=' + exp.toGMTString() : '') + (path ? ';path=' + path : '') + (domain ? ';domain=' + domain : '') + (secure ? ';secure' : '');
};

/**
 * 删除 cookie
 * @param name cookie 名称
 * @param path 路径
 * @param domain 域名
 * @param secure 安全性
 */
$.delCookie = function (name, path, domain, secure) {
    var value = $getCookie(name);
    if (value != null) {
        var exp = new Date();
        exp.setMinutes(exp.getMinutes() - 1000);
        path = path || "/";
        document.cookie = name + '=;expires=' + exp.toGMTString() + (path ? ';path=' + path : '') + (domain ? ';domain=' + domain : '') + (secure ? ';secure' : '');
    }
};

/**
 * 浮点数加法运算
 * @param arg1 被加数
 * @param arg2 加数
 * @return 结果
 */
$.floatAdd = function (arg1, arg2) {
    var r1, r2, m;
    try {
        r1 = arg1.toString().split(".")[1].length;
    } catch (e) {
        r1 = 0;
    }
    try {
        r2 = arg2.toString().split(".")[1].length;
    } catch (e) {
        r2 = 0;
    }
    m = Math.pow(10, Math.max(r1, r2));
    return (arg1 * m + arg2 * m) / m;
};

/**
 * 浮点数减法运算
 * @param arg1 被减数
 * @param arg2 减数
 * @return 结果
 */
$.floatSub = function (arg1, arg2) {
    var r1, r2, m, n;
    try {
        r1 = arg1.toString().split(".")[1].length;
    } catch (e) {
        r1 = 0
    }
    try {
        r2 = arg2.toString().split(".")[1].length;
    } catch (e) {
        r2 = 0
    }
    m = Math.pow(10, Math.max(r1, r2));
    n = (r1 >= r2) ? r1 : r2;
    return ((arg1 * m - arg2 * m) / m).toFixed(n);
};

/**
 * 浮点数乘法运算
 * @param arg1 被乘数
 * @param arg2 乘数
 * @return 结果
 */
$.floatMul = function (arg1, arg2) {
    var m = 0, s1 = arg1.toString(), s2 = arg2.toString();
    try {
        m += s1.split(".")[1].length;
    } catch (e) {
    }
    try {
        m += s2.split(".")[1].length;
    } catch (e) {
    }
    return Number(s1.replace(".", "")) * Number(s2.replace(".", "")) / Math.pow(10, m);
};

/**
 * 浮点数除法运算
 * @param arg1 被除数
 * @param arg2 除数
 * @return 结果
 */
$.floatDiv = function (arg1, arg2) {
    var t1 = 0, t2 = 0, r1, r2;
    try {
        t1 = arg1.toString().split(".")[1].length;
    } catch (e) {
    }
    try {
        t2 = arg2.toString().split(".")[1].length;
    } catch (e) {
    }
    with (Math) {
        r1 = Number(arg1.toString().replace(".", ""));
        r2 = Number(arg2.toString().replace(".", ""));
        return (r1 / r2) * pow(10, t2 - t1);
    }
};


/**
 * 设置数值精度
 * @param value 数值
 * @param scale 精度
 * @param roundingMode 取舍方式
 * @return 指定精度的数值
 */
$.scaleNumber = function (value, scale, roundingMode) {
    if (!roundingMode) {
        roundingMode = "roundhalfup";
    }
    if (roundingMode.toLowerCase() == "roundhalfup") {
        return (Math.round(value * Math.pow(10, scale)) / Math.pow(10, scale)).toFixed(scale);
    } else if (roundingMode.toLowerCase() == "roundup") {
        return (Math.ceil(value * Math.pow(10, scale)) / Math.pow(10, scale)).toFixed(scale);
    } else {
        return (Math.floor(value * Math.pow(10, scale)) / Math.pow(10, scale)).toFixed(scale);
    }
};

/**
 * 将 JSON 对象转换为 STRING
 * @param O JSON 对象
 * @return {String}
 */
$.json2String = function (O) {
    //return JSON.stringify(jsonobj);
    var S = [];
    var J = "";
    if (Object.prototype.toString.apply(O) === '[object Array]') {
        for (var i = 0; i < O.length; i++)
            S.push($.json2String(O[i]));
        J = '[' + S.join(',') + ']';
    } else if (Object.prototype.toString.apply(O) === '[object Date]') {
        J = "new Date(" + O.getTime() + ")";
    } else if (Object.prototype.toString.apply(O) === '[object RegExp]' || Object.prototype.toString.apply(O) === '[object Function]') {
        J = O.toString();
    } else if (Object.prototype.toString.apply(O) === '[object Object]') {
        for (var i in O) {
            O[i] = typeof (O[i]) == 'string' ? '"' + O[i] + '"' : (typeof (O[i]) === 'object' ? $.json2String(O[i]) : O[i]);
            S.push('"' + i + '"' + ':' + O[i]);
        }
        J = '{' + S.join(',') + '}';
    }

    return J;
};

/**
 * 弹出对话框
 * @param opt 对话框配置项
 */
$.showBox = function (opt) {
    var settings = {
        'id': '',
        'modal': true,
        'boxSelector': '',
        'title': '',
        'content': '',
        'icon': '',
        'tip': '',
        'width': 600,
        'height': 0,
        'ajax': '',
        'ajaxText': '<div class="loading-wrap"><i class="icon icon-loading">&nbsp;</i>正在加载……</div>',
        'ok': '',
        'cancel': '',
        'okCallback': null,
        'cancelCallback': function (wrap, settings, btn) {
            wrap.jqmHide();
        }
    };

    $.extend(settings, opt);
    settings.onLoad = function (hash) {
        var loginTip = $(".j-login-tip", $(this));
        if (loginTip.length > 0) {
            $.hideBox({id: settings.id});
            $.showLoginBox(loginTip.text());
            return;
        }
        wrap.css({marginLeft: -(wrap.width() / 2), marginTop: -(wrap.height() / 2)}).jqmShow();
        if (opt.onLoad) {
            opt.onLoad(hash);
        }
    };

    var wrap = $("#_showBox-" + settings.id);
    if (settings.boxSelector == '' && (settings.content != '' || settings.ajax != '')) {
        if (wrap.length <= 0) {
            $('<div id="_showBox-' + settings.id + '" class="modal" style="display: none;"></div>').appendTo($(document.body));
            wrap = $("#_showBox-" + settings.id);
        }
        var innerHtml = '<div class="modal-header"><button class="close jqmClose" type="button">×</button><h3>' + settings.title + '</h3></div>';
        if (settings.icon != '') {
            innerHtml += '<div class="modal-body" style="line-height: 32px;"><i class="icon icon-' + settings.icon + '">&nbsp;</i>';
        } else {
            innerHtml += '<div class="modal-body">';
        }
        if (!settings.content) {
            settings.content = '&nbsp;';
        }
        innerHtml += settings.content + '</div>';
        innerHtml += '<div class="modal-footer">';
        innerHtml += '<div class="tip">' + settings.tip + '</div>';
        if (settings.cancel != '') {
            innerHtml += '<button class="btn btn-darkLight j-cancel-btn">' + settings.cancel + '</button>';
        }
        if (settings.ok != '' || settings.okCallback) {
            if (settings.ok == '') {
                settings.ok = '确定';
            }
            innerHtml += '<button class="btn btn-primary">' + settings.ok + '</button>';
        }
        innerHtml += '</div>';
        wrap.html(innerHtml);
        $(".btn-primary", wrap).click(function () {
            if ($(this).hasClass('disabled')) {
                return false;
            }
            settings.okCallback(wrap, settings, $(this));
        });
        $(".j-cancel-btn", wrap).click(function () {
            if ($(this).hasClass('disabled')) {
                return false;
            }
            settings.cancelCallback(wrap, settings, $(this));
        });
        settings.boxSelector = "#_showBox-" + settings.id;
    }

    wrap = $(settings.boxSelector);
    if (settings.ajax) {
        settings.target = $(".modal-body", wrap);
    }
    wrap.css({marginLeft: -(wrap.width() / 2), marginTop: -(wrap.height() / 2), width: settings.width}).jqm(settings).jqmAddClose('.jqmClose');
    if (settings.height > 0) {
        $('.modal-body').height(settings.height);
    }
    wrap.jqmShow();
};

/**
 * 关闭弹出的对话框
 * @param settings 对话框选择器，回调方法
 */
$.hideBox = function (settings) {
    if (!settings) {
        settings = {};
    }
    if (settings.boxSelector) {
        $(settings.boxSelector).jqmHide();
        return;
    }
    var boxIdSuffix = settings.id || '';
    $("#_showBox-" + boxIdSuffix).jqmHide();
    if (settings.callback) {
        settings.callback();
    }
};

/**
 * 给弹出层添加提示信息
 * @param opt 提示信息配置
 */
$.showBoxTip = function (opt) {
    var settings = {
        id: '',
        extCls: '',
        tipMsg: ''
    };
    $.extend(settings, opt);
    var boxWrap = $("#_showBox-" + settings.id);
    $(".tip", boxWrap).addClass(settings.extCls).html(settings.tipMsg);
};

/**
 * 给弹出层添加提示信息
 * @param opt 提示信息配置
 */
$.setBoxBody = function (opt) {
    var settings = {
        id: '',
        html: ''
    };
    $.extend(settings, opt);
    var boxWrap = $("#_showBox-" + settings.id);
    $(".modal-body", boxWrap).html(settings.html);
};

/**
 * 弹出登陆框
 * @return {Boolean}
 */
$.showLoginBox = function (title) {
    $.hideBox();
    if (!title || !(title instanceof String)) {
        title = '欢迎登陆';
    }
    $.showBox({
        id: 'login-box',
        width: 630,
        title: title,
        content: '<form method="post" action="" id="ajax-login-form"><ul id="j-username-wrap"><li class="shop-form-row"><label for="j-username">用户名</label><input class="width200" type="text" id="j-username" name="j_username" data-pattern="^[a-zA-Z][a-zA-Z0-9_]{3,15}$" data-tip="请输入4－16个字符的用户名"></li><li class="shop-form-row"><label for="j_password">密&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;码</label><input class="width200" type="password" id="j_password" name="j_password" data-pattern="^[\\S]{4,20}$" data-tip="请输入4-20个字符的密码"></li><li class="shop-form-row"><label for="j_captcha">验&nbsp;证&nbsp;码</label><input class="width80" type="text" id="j_captcha" name="j_captcha" data-pattern="^[a-zA-Z0-9]{4}$" data-tip="请输入 4 个字符的验证码"><img class="j-captcha-img" src="" title="点击换一张"/></li></ul></form>',
        ok: '登陆',
        okCallback: function (boxWrap, settings, okBtn) {
            $('#ajax-login-form input').focus();

            var errFields = $("#ajax-login-form .shop-form-error");
            if (errFields.length > 0) {
                $('input', errFields[0]).focus();
                return false;
            }

            var oldText = okBtn.text();
            okBtn.text('正在登陆……').addClass('disabled').attr('disabled', 'disabled');
            $.iAjax({
                url: Isnowing.base + 'j_spring_security_check?ajax=true',
                data: $("#ajax-login-form").serialize(),
                doSuccess: function (data) {
                    window.location.reload();
                },
                doFailure: function (data) {
                    okBtn.text(oldText).removeClass('disabled').removeAttr('disabled');
                    $(".tip", boxWrap).addClass('error').text(data.message);
                    $(".j-captcha-img").click();
                }
            });
        },
        onShow: function (h) {
            h.w.show();
            $(".j-captcha-img").attr("src", Isnowing.base + "static/captcha.jpg?timestamp=" + (new Date()).valueOf());
            $("#ajax-login-form").validationForm();
        }
    });
    return false;
};

$.iAjax = function (opt) {
    var settings = {
        type: "POST",
        dataType: 'json',
        loadingTitle: '',
        loadingContent: '',
        doSuccess: function (data, textStatus, jqXHR) {
        },
        doFailure: function (data, textStatus, jqXHR) {
            $.showBox({'title': '操作失败', 'icon': data.status, 'content': data.message});
        },
        error: function (request, status, thrown) {
            console.log(request.status + ":" + request.readyState);
            console.log(status);
            console.log(thrown);
            if (settings.loadingContent != '') {
                var loadingTitle = settings.loadingTitle;
                if (loadingTitle == '') {
                    loadingTitle = '操作失败';
                }
                $.showBox({'title': loadingTitle, 'icon': 'error', 'content': '操作失败，请稍候重试！'});
            } else {
                alert('操作失败，请稍候重试！');
            }
        },
        beforeSend: function () {
            if (settings.loadingContent != '') {
                var loadingTitle = settings.loadingTitle;
                if (loadingTitle == '') {
                    loadingTitle = '正在处理';
                }
                $.showBox({'title': loadingTitle, 'icon': 'loading', 'content': settings.loadingContent});
            }
        },
        success: function (data, textStatus, jqXHR) {
            if (settings.loadingContent != '') {
                $.hideBox({});
            }
            if (data.status == 'auth') {
                $.showBox({'icon': 'error', 'title': '操作超时', 'content': data.message, 'ok': '去登陆', 'okCallback': $.showLoginBox});
                return;
            }
            if (data.status == 'error') {
                settings.doFailure(data, textStatus, jqXHR);
            } else {
                settings.doSuccess(data, textStatus, jqXHR);
            }
        }
    };
    $.extend(settings, opt);
    if (!settings.data) {
        settings.data = {};
    }
    settings.data['ajax'] = true;
    if (settings.loadingTitle != '' && settings.loadingContent == '') {
        settings.loadingContent = settings.loadingTitle + '...';
    }
    $.ajax(settings);
};

/**
 * 滚动组件
 * @param opt 配置
 */
$.fn.freeScroll = function (opt) {
    var settings = {
        "preSelector": '',
        "nextSelector": '',
        "preDisabledClass": 'disabled',
        "nextDisabledClass": 'disabled',
        "direction": 'vertical',
        callback: function (current, totalTriggerCount, obj) {
        }
    };

    $.extend(settings, opt);

    return $(this).each(function () {
        var isVertical = settings.direction == 'vertical';
        var wrapper = $(this);
        var delta = isVertical ? $(":first-child", wrapper).outerHeight(true) : $(":first-child", wrapper).outerWidth(true);
        var totalTriggerCount = isVertical ? parseInt(wrapper.height() / delta) : wrapper.children().length - parseInt(wrapper.width() / delta);
        var current = 0;

        var moreDimension = delta * totalTriggerCount;
        isVertical ? wrapper.height(wrapper.height() + moreDimension) : wrapper.width(wrapper.width() + moreDimension);

        $(opt.nextSelector).bind('click', function () {
            if (current == totalTriggerCount || $(this).hasClass(settings.nextDisabledClass)) {
                return false;
            }
            isVertical ? wrapper.animate({marginTop: '-=' + delta}) : wrapper.animate({marginLeft: '-=' + delta});
            current++;
            if (current == totalTriggerCount) {
                $(this).addClass(settings.nextDisabledClass);
            }
            $(opt.preSelector).removeClass(settings.preDisabledClass);
            settings.callback(current, totalTriggerCount, wrapper);
            return false;
        });

        $(opt.preSelector).bind('click', function () {
            if (current == 0 || $(this).hasClass(settings.preDisabledClass)) {
                return false;
            }
            isVertical ? wrapper.animate({marginTop: '+=' + delta}) : wrapper.animate({marginLeft: '+=' + delta});
            current--;
            if (current == 0) {
                $(this).addClass(settings.preDisabledClass);
            }
            $(opt.nextSelector).removeClass(settings.nextDisabledClass);
            settings.callback(current, totalTriggerCount, wrapper);
            return false;
        });
    });
};

/**
 * 限制性数值输入框
 * @param opt 配置
 */
$.fn.limitInput = function (opt) {
    var settings = {
        max: 0,
        min: 0,
        lowerflow: function () {
        },
        overflow: function () {
        }
    };
    $.extend(settings, opt);
    return $(this).each(function () {
        if (settings.max == 0 && $(this).attr('data-max')) {
            settings.max = parseInt($(this).attr('data-max'));
        }
        if (settings.min == 0 && $(this).attr('data-min')) {
            settings.min = parseInt($(this).attr('data-min'));
        }
        $(this).keydown(function (event) {
            if ((event.which < 48 || event.which > 57) && event.which != 8 && event.which != 86) {
                return false;
            }
        }).keyup(function () {
            var val = parseInt($(this).val());
            if (val < settings.min) {
                $(this).val(settings.min);
                settings.lowerflow($(this));
            } else if (val > settings.max) {
                $(this).val(settings.max);
                settings.overflow($(this));
            }
        });
    });
};

/**
 * 简单 tab 组件
 * @param opt 配置
 */
$.fn.simpleTab = function (opt) {
    var settings = {
        triggerType: 'click',
        activeClass: 'current',
        targetProp: 'href',
        callback: function (currentTab, tabGroup) {
        }
    };
    $.extend(settings, opt || {});
    return $(this).each(function () {
        var tabs = $(this).children();
        $(tabs).each(function () {
            $(this).bind(settings.triggerType, function () {
                tabs.removeClass(settings.activeClass);
                $(this).addClass(settings.activeClass);

                tabs.each(function () {
                    $($(this).attr(settings.targetProp)).hide();
                });

                $($(this).attr(settings.targetProp)).show();

                settings.callback($(this), tabs);
                return false;
            });
        });
        $(tabs[0]).trigger(settings.triggerType);
    });
};

(function ($) {
    $.fn.extend({
        /**
         * 可伸缩的表格
         * @return {*}
         */
        resizableTable: function (opt) {
            var settings = {
                removeTrigger: '.j-remove',
                confirm: false,
                addTrigger: '.j-add',
                minSize: 1,
                clearClonedData: false
            };
            $.extend(settings, opt || {});
            return this.each(function (opt) {
                var _table = $(this);
                $(settings.removeTrigger, _table).click(function () {
                    if ($('tbody tr', _table).length <= settings.minSize) {
                        alert('至少保留' + settings.minSize + '行数据！');
                        return false;
                    }
                    if (settings.confirm === true) {
                        if (window.confirm('您确认要删除该行数据吗？')) {
                            $(this).parents('tr').remove();
                        }
                    } else {
                        $(this).parents('tr').remove();
                    }
                    return false;
                });
                $(settings.addTrigger, _table).click(function () {
                    var cloned = $($('tbody tr', _table).get(0)).clone(true);
                    if (settings.clearClonedData === true) {
                        $('input, textarea', cloned).val();
                    }
                    _table.append(cloned);
                    return false;
                });
            });
        }
    });
})(jQuery);

/**
 * 表单 验证 插件
 */
(function ($) {
    $.fn.extend({
        validationForm: function () {
            return this.each(function () {
                $("input, textarea", $(this)).focus(function () {
                    if ($(this).attr('data-tip') != '') {
                        $(this).parents('.shop-form-row').find('.form-row-tip').text($(this).attr('data-tip'));
                        $(this).siblings('.form-row-explain').show();
                    }
                }).blur(function () {
                    if (!$(this).attr('data-pattern') || $(this).attr('data-pattern') == '') {
                        return;
                    }
                    var explain = $(this).parents('.shop-form-row').find('.form-row-explain');
                    if (explain.length == 0) {
                        explain = $('<div class="form-row-explain"><em class="shop-form-row-arrow"></em><span class="form-row-tip">' + $(this).attr('data-tip') + '</span></div>');
                        $(this).after(explain);
                        explain.show();
                    }
                    if (!(new RegExp($(this).attr('data-pattern')).test($(this).val()))) {
                        $(this).parents('.shop-form-row').removeClass('shop-form-success');
                        $(this).parents('.shop-form-row').addClass('shop-form-warn');
                    } else {
                        $(this).parents('.shop-form-row').removeClass('shop-form-warn');
                        $(this).parents('.shop-form-row').addClass('shop-form-success');
                    }
                });

                $(this).submit(function () {
                    $('input', $(this)).focus();

                    var errFields = $(this).find(".shop-form-warn");
                    if (errFields.length > 0) {
                        $('input', errFields[0]).focus();
                        return false;
                    }
                    return true;
                });
            });
        }
    })
})(jQuery);