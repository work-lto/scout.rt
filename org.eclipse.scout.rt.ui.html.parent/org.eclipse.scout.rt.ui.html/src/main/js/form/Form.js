scout.Form = function() {
  scout.Form.parent.call(this);
  this._$title;
  this._$parent;
  this.rootGroupBox;
  this.menus = [];
  this.staticMenus = [];
  this._addAdapterProperties(['rootGroupBox', 'menus']);
  this._locked;
};
scout.inherits(scout.Form, scout.ModelAdapter);

scout.Form.prototype._render = function($parent) {
  this._$parent = $parent;
  this.$container = $('<div>').
    appendTo($parent).
    attr('id', 'Form-' + this.id).
    addClass('form').
    data('model', this);

  var layout = new scout.FormLayout();
  scout.Layout.setLayout(this.$container, layout);

  this.rootGroupBox.render(this.$container);

  var closeable = false;
  var detachable = true; // FIXME BSH: How to determine 'detachable' property?
  if (window.scout.sessions.length > 1 || this.session.parentJsonSession) {
    // Cannot detach if...
    // 1. there is more than one session inside the window (portlets), because
    //    we would not know which session to attach to.
    // 2. the window is already a child window (cannot detatch further).
    detachable = false;
  }

  // TODO AWE: (button-bar) move to GroupBox / add layout / see SSGroupBoxButtonBar
  var systemButtons = this.rootGroupBox.getSystemButtons();
  if (systemButtons) {
    var $buttonBar = $.makeDiv('', 'button-bar').
      appendTo(this.$container);
    var i, button;
    for (i = 0; i < systemButtons.length; i++) {
      button = systemButtons[i];
      button.render($buttonBar);
      if (button.visible && (button.systemType == scout.Button.SYSTEM_TYPE.CANCEL || button.systemType == scout.Button.SYSTEM_TYPE.CLOSE)) {
        closeable = true;
      }
    }
  }

  // TODO AWE: append form title section (including ! ? and progress indicator)
  this.menubar = new scout.Menubar(this.$container);
  this.menubar.menuTypesForLeft1 = ['Form.System'];
  this.menubar.menuTypesForLeft2 = ['Form.Regular'];
  this.menubar.menuTypesForRight = ['Form.Tool'];
  this.menubar.staticMenus = this.staticMenus;
  this.menubar.updateItems(this.menus);

  if (closeable) {
    var $closeButton = $('<button>').text('X');
    this.menubar.$container.append($closeButton);
    $closeButton.on('click', function() {
      this.session.send('formClosing', this.id);
    }.bind(this));
  }
  if (detachable) {
    var $detachButton = $('<button>').text('D').attr('title', "Detach form");
    this.menubar.$container.append($detachButton);
    $detachButton.on('click', function() {
      // FIXME BSH Set correct url or write content
      //        w.document.write('<html><head><title>Test</title></head><body>Hello</body></html>');
      //        w.document.close(); //finish "loading" the page
      var childWindow = scout.openWindow(window.location.href, 'scout:form:' + this.id, 800, 600);
      $(childWindow).one('load', function() {
        // Cannot call this directly, because we get an unload event right after that (and
        // would therefore unregister the window again). This is because the browser starts
        // with the 'about:blank' page. Opening the desired URL causes the blank page to unload.
        // Therefore, we wait until the target page was loaded.
        this.session.registerChildWindow(childWindow);
      }.bind(this));
    }.bind(this));
  }

  layout.layout(this.$container);

  if (this._locked) {
    this.disable();
  }
};

scout.Form.prototype._remove = function() {
  scout.Form.parent.prototype._remove.call(this);

  if (this.$glasspane) {
    this.$glasspane.remove();
  }
};

scout.Form.prototype.appendTo = function($parent) {
  this.$container.appendTo($parent);
};

// TODO AWE: (C.GU) hier sollten wir doch besser die setEnabled() method verwenden / überscheiben.
scout.Form.prototype.enable = function() {
  //FIXME CGU implement
};

scout.Form.prototype.disable = function() {
};

scout.Form.prototype.onModelCreate = function() {};

scout.Form.prototype.onModelAction = function(event) {
  if (event.type === 'formClosed') {
    this.destroy();
  } else {
    $.log('Model event not handled. Widget: Form. Event: ' + event.type + '.');
  }
};
