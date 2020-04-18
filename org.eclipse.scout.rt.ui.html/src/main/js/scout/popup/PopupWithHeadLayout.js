/*******************************************************************************
 * Copyright (c) 2014-2018 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
scout.PopupWithHeadLayout = function(popup) {
  scout.PopupWithHeadLayout.parent.call(this, popup);
};
scout.inherits(scout.PopupWithHeadLayout, scout.PopupLayout);

scout.PopupWithHeadLayout.prototype._setSize = function(prefSize) {
  scout.PopupWithHeadLayout.parent.prototype._setSize.call(this, prefSize);

  var htmlComp = this.popup.htmlComp,
    popupSize = prefSize,
    htmlBody = scout.HtmlComponent.optGet(this.popup.$body);

  // While animating the body animation sets the size
  if (!this.popup.bodyAnimating) {
    // Set size of body
    popupSize = popupSize.subtract(htmlComp.insets());
    if (this._headVisible) {
      var headSize = scout.graphics.size(this.popup.$head, true);
      // Adjust popup size if head changed size
      if (popupSize.width < headSize.width) {
        popupSize.width = headSize.width;
      }
    }
    htmlBody.setSize(popupSize);
  }
};

/**
 * @override
 */
scout.PopupWithHeadLayout.prototype._adjustSize = function(prefSize) {
  return this._adjustSizeWithAnchor(prefSize);
};

/**
 * @override
 */
scout.PopupWithHeadLayout.prototype._calcMaxSizeAroundAnchor = function() {
  if (!this.popup._headVisible) {
    return scout.PopupWithHeadLayout.parent.prototype._calcMaxSizeAroundAnchor.call(this);
  }

  // Position the popup at the desired location before doing any calculations,
  // but do no try to switch the position if there is not enough space
  this.popup.position(false);

  var maxWidthLeft, maxWidthRight, maxHeightDown, maxHeightUp,
    htmlComp = this.popup.htmlComp,
    windowPaddingLeft = this.popup.windowPaddingX,
    windowPaddingRight = this.popup.windowPaddingX,
    windowPaddingY = this.popup.windowPaddingY,
    popupBounds = scout.graphics.offsetBounds(htmlComp.$comp),
    popupHeadBounds = scout.graphics.offsetBounds(this.popup.$head),
    popupMargins = htmlComp.margins(),
    windowSize = this.popup.getWindowSize();

  maxWidthRight = windowSize.width - popupHeadBounds.x - windowPaddingRight;
  maxWidthLeft = popupHeadBounds.x + popupHeadBounds.width - windowPaddingLeft;
  maxHeightDown = (windowSize.height - popupBounds.y - windowPaddingY);
  // head height is irrelevant because popup has a margin as height as the header
  maxHeightUp = (popupBounds.y - popupMargins.vertical() - windowPaddingY);

  return new scout.Insets(maxHeightUp, maxWidthRight, maxHeightDown, maxWidthLeft);
};

scout.PopupWithHeadLayout.prototype.preferredLayoutSize = function($container, options) {
  var htmlComp = this.popup.htmlComp,
    htmlBody,
    prefSize;

  if (!this.popup.bodyAnimating) {
    var popupStyleBackup = this.popup.$container.attr('style');
    var $siblingBodies = this.popup.$body.siblings('.popup-body');
    $siblingBodies.addClass('hidden');
    this.popup.$container.css({
      width: 'auto',
      height: 'auto'
    });

    htmlBody = scout.HtmlComponent.optGet(this.popup.$body);
    if (htmlBody) {
      prefSize = htmlBody.prefSize(options)
        .add(htmlBody.margins());
    } else {
      prefSize = scout.graphics.prefSize(this.popup.$body, options)
        .add(scout.graphics.margins(this.popup.$body));
    }

    $siblingBodies.removeClass('hidden');
    this.popup.$container.attr('style', popupStyleBackup);
  } else {
    prefSize = scout.graphics.size(this.popup.$body, options)
      .add(scout.graphics.margins(this.popup.$body));
  }

  if (this.popup._headVisible) {
    var headSize = scout.graphics.size(this.popup.$head, options)
      .add(scout.graphics.margins(this.popup.$head));
    prefSize.width = prefSize.width < headSize.width ? headSize.width : prefSize.width;
  }
  prefSize.add(htmlComp.insets());
  return prefSize;
};