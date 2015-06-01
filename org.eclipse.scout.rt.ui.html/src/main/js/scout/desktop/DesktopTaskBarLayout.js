scout.DesktopTabBarLayout = function(desktop) {
  scout.DesktopTabBarLayout.parent.call(this);

  this.TAB_WIDTH_LARGE = 220;
  this.TAB_WIDTH_SMALL = 130;
  this._desktop = desktop;
  this._$overflowTab;
  this._overflowTabsIndizes = [];
};
scout.inherits(scout.DesktopTabBarLayout, scout.AbstractLayout);

// create a clone to measure pref. width
scout.DesktopTabBarLayout.prototype._toolsWidth = function($tools, cssClasses) {
  var $clone = $tools.clone(),
    $items = $clone.find('.taskbar-tool-item');

  $items
    .removeClass('min-padding')
    .removeClass('icon-only');

  if (cssClasses) {
    $items.addClass(cssClasses);
  }
  $('body').append($clone).width('auto');
  var toolsWidth = scout.graphics.getSize($clone, true).width;
  $clone.remove();
  return toolsWidth;
};

/**
 * @override AbstractLayout.js
 */
scout.DesktopTabBarLayout.prototype.layout = function($container) {
  var $tabs = $container.find('.taskbar-tabs'),
    $tools = $container.find('.taskbar-tools'),
    $logo = $container.find('.taskbar-logo'),
    contWidth = scout.graphics.getSize($container).width,
    logoWidth = scout.graphics.getSize($logo, true).width,
    numTabs = this._desktop.tabCount(),
    largePrefTabsWidth = numTabs * this.TAB_WIDTH_LARGE,
    smallPrefTabsWidth = numTabs * this.TAB_WIDTH_SMALL,
    toolsWidth, tabsWidth;

  // reset tabs and tool-items
  if (this._$overflowTab) {
    this._$overflowTab.remove();
  }

  $tabs.find('.taskbar-tab-item').setVisible(true);

  $tools.find('.taskbar-tool-item').each(function() {
    var $item = $(this);
    $item.removeClass('min-padding');
    var dataText = $item.data('item-text');
    if (dataText) {
      $item.text(dataText);
    }
  });

  toolsWidth = this._toolsWidth($tools);
  tabsWidth = contWidth - toolsWidth - logoWidth;

  $.log.info('numTabs=' + numTabs + ' contWidth=' + contWidth + ' toolsWidth=' + toolsWidth + ' logoWidth=' + logoWidth + ' --> tabsWidth=' + tabsWidth);
  $tools.cssLeft(contWidth - toolsWidth - logoWidth);

  this._overflowTabsIndizes = [];
  var tabWidth;
  if (smallPrefTabsWidth <= tabsWidth) {
    tabWidth = Math.min(this.TAB_WIDTH_LARGE, Math.floor(tabsWidth / numTabs));
    $.log.info('MEEP(1) tabWidth=' + tabWidth);
    // 2nd - all Tabs fit when they have small size
    $tabs.find('.taskbar-tab-item').each(function() {
      $(this).outerWidth(tabWidth);
    });
  }
  else {

    // 1st try to minimize padding around tool-bar items
    // re-calculate tabsWidth with reduced padding on the tool-bar-items
    $tools.find('.taskbar-tool-item').each(function() {
      $(this).addClass('min-padding');
    });

    toolsWidth = scout.graphics.getSize($tools, true).width;
    tabsWidth = contWidth - toolsWidth - logoWidth;
    $tools.cssLeft(contWidth - toolsWidth - logoWidth);
    $.log.info('MEEP(2) min-padding tabsWidth=' + tabsWidth + ' toolsWidth=' + toolsWidth);

    if (smallPrefTabsWidth <= tabsWidth) {
      tabWidth = this.TAB_WIDTH_SMALL;
      $tabs.find('.taskbar-tab-item').each(function() {
        $(this).outerWidth(tabWidth);
      });
      return;
    }

    // 2nd remove text from tool-bar items, only show icon
    $tools.find('.taskbar-tool-item').each(function() {
      var $item = $(this),
        text = $item.text();
      $item.empty();
      $item.data('item-text', text);
    });

    toolsWidth = scout.graphics.getSize($tools, true).width;
    tabsWidth = contWidth - toolsWidth - logoWidth;
    $tools.cssLeft(contWidth - toolsWidth - logoWidth);
    $.log.info('MEEP(3) icon-only tabsWidth=' + tabsWidth + ' toolsWidth=' + toolsWidth);

    if (smallPrefTabsWidth <= tabsWidth) {
      tabWidth = this.TAB_WIDTH_SMALL;
      $tabs.find('.taskbar-tab-item').each(function() {
        $(this).outerWidth(tabWidth);
      });
      return;
    }

    // Still doesn't fit? Put tabs into overflow menu
    tabsWidth -= 30;

    // check how many tabs fit into remaining tabsWidth
    var numVisibleTabs = Math.floor(tabsWidth / this.TAB_WIDTH_SMALL),
      numOverflowTabs = numTabs - numVisibleTabs;
    $.log.info('MEEP(4) icon-only tabsWidth=' + tabsWidth + ' toolsWidth=' + toolsWidth + ' numVisibleTabs=' + numVisibleTabs + ' numOverflowTabs=' + numOverflowTabs);

    // FIXME AWE: display correct range of tabs (around visible tab)
    // FIXME AWE: tabs have no 'selected' state, this must be added together with activeForm on model Desktop
    // Never put first tab into overflow (this will change when desktop is refactored)
    var i = 0, selectedIndex, tab;
    $tabs.find('.taskbar-tab-item').each(function() {
      if ($(this).hasClass('selected')) {
        selectedIndex = i;
      }
      i++;
    });

    // determine visible range
    var rightEnd, leftEnd = selectedIndex - Math.floor(numVisibleTabs / 2);
    if (leftEnd < 0) {
      leftEnd = 0;
      rightEnd = numVisibleTabs - 1;
    } else {
      rightEnd = leftEnd + numVisibleTabs - 1;
      if (rightEnd > numTabs - 1) {
        rightEnd = numTabs - 1;
        leftEnd = rightEnd - numVisibleTabs + 1;
      }
    }
    $.log.info('show items from leftEnd=' + leftEnd + ' to rightEnd=' + rightEnd + ' selectedIndex=' + selectedIndex);

    this._$overflowTab = $tabs
      .appendDiv('overflow-tab-item')
      .on('click', this._onClickOverflow.bind(this));
    if (numOverflowTabs > 1) {
      this._$overflowTab.appendDiv('num-tabs').text(numOverflowTabs);
    }

    var that = this;
    tabWidth = this.TAB_WIDTH_SMALL;
    i = 0;
    $tabs.find('.taskbar-tab-item').each(function() {
      if (i >= leftEnd && i <= rightEnd) {
        $(this).outerWidth(tabWidth);
      } else {
        $(this).setVisible(false);
        that._overflowTabsIndizes.push(i);
      }
      i++;
    });
  }
};

scout.DesktopTabBarLayout.prototype._onClickOverflow = function(event) {
  var menu, tab, text, popup, overflowMenus = [], session = this._desktop.session, allTabs = this._desktop._allTabs;
  this._overflowTabsIndizes.forEach(function(i) {
    tab = allTabs[i];
    text = tab.title;
    if (tab.subTitle) {
      text += ' (' + tab.subTitle + ')';
    }
    menu = session.createUiObject({
      objectType: 'Menu',
      text: text
    });
    overflowMenus.push(menu);
  });

  $.log.info('XXX click overflow ' + this._overflowTabsIndizes);
  popup = new scout.ContextMenuPopup(session, overflowMenus);
  popup.render();
  popup.setLocation(new scout.Point(event.pageX, event.pageY));
};
