scout.Scrollbar2 = {};

/**
 * Static function to install a scrollbar on a container.
 * When the client supports pretty native scrollbars, we use them by default.
 * Otherwise we install JS-based scrollbars. In that case the install function
 * adds an additional 'viewport' DIV to the DOM. For native scrollbars we
 * must set some additional CSS styles.
 *
 * The function returns either the $container (when native) or the additional
 * viewport (when JS-based), you should use the returned DOM element when you
 * add elements.
 */
// TODO AWE: (scrollbar) merge with Scrollbar.js or rename to ScrollbarWrapper(?)
scout.Scrollbar2.install = function($container, options) {
  var prettyScrollbars = false;
  if (prettyScrollbars) {
    $.log.debug('use native scrollbars for container ' + scout.graphics.debugOutput($container));
    $container.
      css('overflow', 'hidden').
      css('overflow-y', 'auto').
      css('-webkit-overflow-scrolling', 'touch');
      // TODO AWE: (scrollbar) add other styles based on config-object and used browser, for instance:
      // -webkit-overflow-scrolling: touch;
    return $container;
  } else {
    $.log.debug('install JS-scrollbars for container ' + scout.graphics.debugOutput($container) +  ' and add viewport DIV');
    var $viewport = $container.appendDIV('viewport'),
      scrollbar = new scout.Scrollbar($viewport, options);
    scrollbar.initThumb();
    $viewport.data('scrollbar', scrollbar);
    return $viewport;
  }
};

scout.Scrollbar2.update = function($viewport) {
  var scrollbar = $viewport.data('scrollbar');
  if (scrollbar) {
    scrollbar.initThumb();
  }
};

/**
 * Scrolls the viewport to the given object (must be a child of viewport)
 * @param $viewport
 * @param $selection
 */
scout.Scrollbar2.scrollTo = function($viewport, $selection) {
  var scrollDir = 'n/a',
    scrollTop = $viewport.scrollTop(),
    viewportH = $viewport.height(),
    optionH = scout.graphics.getSize($selection).height,
    optionY = $selection.position().top;
  $.log.debug('scrollTo() scrollTop=' + scrollTop + ' viewportH=' + viewportH + ' optionH=' + optionH + ' optionY=' + optionY);

  if (optionY < 0) {
    scrollDir = 'Up';
    $viewport.scrollTop(scrollTop + optionY);
  } else if (optionY + optionH > viewportH) {
    scrollDir = 'Down';
    $viewport.scrollTop(scrollTop + optionY + optionH - viewportH);
  }

  $.log.debug('_scrollToSelection scrollDir=' + scrollDir);
};
