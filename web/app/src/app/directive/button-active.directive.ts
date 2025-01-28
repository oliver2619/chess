import {Directive, HostBinding, Input} from '@angular/core';

@Directive({
  selector: '[cButtonActive]'
})
export class ButtonActiveDirective {

  @Input('cButtonActive')
  @HostBinding('class.active')
  active = false;
}
