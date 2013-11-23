goog.provide('sample.Application');
goog.require('goog.dom');

/**
 * @constructor
 * @param {Element} element
 */
sample.Application = function(element) {
  this.element = element;
  this.stepInternal();
};
goog.exportSymbol("sample.Application", sample.Application);

/**
 *
 */
sample.Application.prototype.stepInternal = function() {
  goog.dom.setTextContent(this.element, goog.now());
};