$().ready(function () {

    var _pageId = $('#j-page-dbId').val();
    var _columns = $('.column_box');

    /**
     * 开启模块编辑功能，目前只是示例，并不真正支持编辑功能
     */
    $(".module_box").live('hover', function () {
        var mask = $(".module_edit_mask");
        if (mask.length == 0) {
            mask = $('<div class="module_edit_mask" title="双击编辑模块"><div class="module_mask_bd"></div><a class="mod_opt_btn j-remove-module icon-font" title="删除">&#xe602;</a><a class="mod_opt_btn j-edit-module icon-font" title="编辑">&#xe603;</a></div>');

            // 编辑模块
            $('.j-edit-module', mask).click(function () {
                $.editCompParams($(this).parents(".module_box"));
                return false;
            });
            // 删除模块
            $(".j-remove-module", mask).click(function () {
                var _this = $(this);
                if (_this.attr('data-removable') == '0') {
                    $.showBox({
                        icon: 'error',
                        title: '删除模块',
                        content: '对不起，该模块不能被删除！'
                    });
                    return false;
                }
                if (window.confirm('您确定要删除该模块吗')) {
                    var module = _this.parents(".module_box");
                    var delModHolder = $("#j-del-modules-holder");
                    delModHolder.val(delModHolder.val() + ',' + module.attr('data-inst-id'));
                    $.layoutChanged(module.parents(".layout_box"));
                    module.remove();
                }
                return false;
            });
        }
        if ($(this).attr('data-removable') == '0') {
            $(".j-remove-module", mask).hide();
        } else {
            $(".j-remove-module", mask).show();
        }

        mask.width($(this).width() - 2).height($(this).height() - 2).appendTo($(this)).show();
    });

    // 模块拖拽
    _columns.dragsort({
        dragSelector: ".module_box",
        itemSelector: ".module_box",
        dragBetween: true,
        dragEnd: $.pageChanged,
        placeHolderTemplate: "<div class='module_box module_placeholder'></div>"
    });

    // 添加新的列
    var currAddColTrigWrap;
    $('.layout_box').each(function (index, layout) {
        if (!$(layout).hasClass('column_ignore')) {
            $(layout).append('<div class="item_settings_wrap column_add_wrap"><a href="#" class="j-add-column"><div class="item_add_content">新增一列</div></a></div>');
        }
    });
    $('.j-add-column').click(function () {
        currAddColTrigWrap = $(this).parents(".column_add_wrap");
        currAddColTrigWrap.before('<div class="column_box width25p" data-inst-id="0"><div class="item_settings_wrap"><a href="#" class="j-add-module btn btn-primary mr10">添加模块</a><a href="#" class="j-column-edit btn btn-blue">编辑列</a></div>');

        var _cols = $('.column_box');
        _cols.removeClass('width50p');
        _cols.removeClass('width33p');
        _cols.removeClass('width25p');
        _cols.removeClass('width20p');
        _cols.removeClass('width10p');
        if (_cols.length == 1) {
            _cols.addClass('width50p');
        } else if (_cols.length == 2) {
            _cols.addClass('width33p');
        } else if (_cols.length == 3) {
            _cols.addClass('width25p');
        } else if (_cols.length == 4) {
            _cols.addClass('width20p');
        } else {
            _cols.addClass('width10p');
        }
        _cols.dragsort("destroy");
        _cols.dragsort({
            dragSelector: ".module_box",
            itemSelector: ".module_box",
            dragBetween: true,
            dragEnd: $.pageChanged,
            placeHolderTemplate: "<div class='module_box module_placeholder'></div>"
        });
    });

    // 添加模块
    var currAddModTrigWrap;
    _columns.each(function (index, column) {
        if (!$(column).hasClass('column_ignore')) {
            $(column).append('<div class="item_settings_wrap"><a href="#" class="j-add-module btn btn-primary mr10">添加模块</a><a href="#" class="j-column-edit btn btn-blue">编辑列</a></div>');
        }
    });
    $('.j-column-edit').live('click', function () {
        $.editCompParams($(this).parents(".column_box"));
        return false;
    });
    $(".j-add-module").live('click', function () {
        currAddModTrigWrap = $(this).parents(".item_settings_wrap");
        $.showBox({
            title: '新增模块列表',
            ajax: '/cms/design/module-prototype-list.html',
            onLoad: function () {
                $(".modal_list_item .j-add-module-btn").click(function () {
                    $.showBox({
                        title: '正在添加模块',
                        icon: 'load'
                    });
                    $.iAjax({
                        dataType: 'html',
                        url: $(this).attr('href'),
                        doSuccess: function (data) {
                            $.hideBox();
                            currAddModTrigWrap.before(data);
                            $.layoutChanged(currAddModTrigWrap.parents(".layout_box"));
                        }
                    });
                    return false;
                });
            }
        });
        return false;
    });

    // 编辑参数
    $.editCompParams = function (comp) {
        var instanceId = comp.attr('data-inst-id');
        var instanceTypeTag = comp.hasClass('module_box') ? "module"
            : (comp.hasClass('column_box') ? "column"
            : (comp.hasClass('layout_box') ? "layout"
            : (comp.hasClass('page_box' ? "page" : ""))));
        if (instanceId == '0') {
            $.showBox({
                icon: 'ask',
                title: '操作提示',
                content: '该模块为新添加的模块，未保存页面结构前不可编辑，现在保存页面结构吗？',
                ok: '保存',
                okCallback: $.savePageLayout,
                cancel: '取消'
            });
            return false;
        }
        $.showBox({
            title: '编辑组件参数',
            width: 760,
            ajax: '/cms/design/render-comp-form.html?instanceId=' + instanceId + '&instanceTypeTag=' + instanceTypeTag + '&pageId=' + _pageId,
            onLoad: function () {
                var modInstForm = $('.modal form');
                if (modInstForm.length == 0) {
                    $(".modal-footer").html('');
                    return;
                }
                $('.module_form_table', modInstForm).resizableTable();
                modInstForm.submit(function () {
                    var _form = $(this);
                    $.iAjax({
                        loadingTitle: '保存参数',
                        loadingContent: '正在保存模块参数，请稍候……',
                        url: _form.attr('action'),
                        data: _form.serialize(),
                        doSuccess: function (data) {
                            var _wrap = $(data.data.compContent);
                            if (instanceTypeTag == 'column' || instanceTypeTag == 'layout') {
                                var _tmp = comp.html();
                                if (instanceTypeTag == 'column') {
                                    $('column_box', _wrap).html(_tmp);
                                } else {
                                    $('layout_box', _wrap).html(_tmp);
                                }
                            }
                            comp.before(_wrap);
                            comp.remove();
                            $.hideBox();
                        },
                        doFailure: function (data) {
                            $.showBoxTip({
                                extCls: 'error',
                                tipMsg: data.message
                            });
                        }
                    });
                    return false;
                });
            },
            ok: '保存',
            okCallback: function () {
                $("#_comp_form_" + instanceId).submit();
            }
        });
    };

    // 页面结构变化
    $.pageChanged = function () {
        var saveTipWrap = $(".save_tip_wrap");
        if (saveTipWrap.length <= 0) {
            saveTipWrap = $('<div class="save_tip_wrap"><div class="save_tip_content">您对页面进行了修改，请及时<a href="#" class="save_tip_btn">保存</a><a href="#" class="cancel_tip_btn">撤销修改</a></div></div>');
            $(document.body).append(saveTipWrap);
            $(".save_tip_btn", saveTipWrap).click($.savePageLayout);
            $(".cancel_tip_btn", saveTipWrap).click(function () {
                if (window.confirm('您确定要撤销所有的页面修改吗？')) {
                    window.location.reload();
                }
                return false;
            });
        } else {
            saveTipWrap.show();
        }

        // 将 添加模块 的按钮移动到底部
        $('.item_settings_wrap').each(function (index, elem) {
            $(this).appendTo($(this).parents('.column_box'));
        });
    };

    // 判断头尾是否变化
    $.layoutChanged = function (layout) {
        if (layout.attr('data-name') == 'header') {
            $("#j-header-changed-holder").val("true");
        } else if (layout.attr('data-name') == 'footer') {
            $("#j-footer-changed-holder").val("true");
        }
        $.pageChanged();
    };

    $.savePageLayout = function () {
        var page = {};
        $(".layout_box").each(function (index, layout) {
            if (!$(layout).hasClass('layout_ignore')) {
                var layoutPrototypeId = $(layout).attr('data-proto-id');
                var layoutInstId = $(layout).attr('data-inst-id');
                var columnData = {};
                $(".column_box", $(layout)).each(function (index, column) {
                    if (!$(column).hasClass('column_ignore')) {
                        var colProtoId = $(column).attr('data-proto-id');
                        var colInstId = $(column).attr('data-inst-id');
                        columnData[colProtoId + ',' + colInstId] = $(".module_box", $(column)).map($.mapModuleData).get().join("|");
                    }
                });
                page[layoutPrototypeId + ',' + layoutInstId] = columnData;
            }
        });
        $("#j-page-layout-holder").val($.json2String(page));
        var _saveForm = $("#j-page-form");
        $.post(_saveForm.attr('action'), _saveForm.serialize(), function (data) {
            console.log(data);
        });

        return false;
    };

    $.mapModuleData = function (modElem) {
        return $(this).attr('data-proto-id') + "," + $(this).attr('data-inst-id');
    };
});