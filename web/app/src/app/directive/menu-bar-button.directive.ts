import {ComponentRef, Directive, ElementRef, Input, OnDestroy, Type, ViewContainerRef} from '@angular/core';
import {GameMenuComponent} from '../element/game-menu/game-menu.component';
import {SessionMenuComponent} from '../element/session-menu/session-menu.component';
import {OpeningsMenuComponent} from '../element/openings-menu/openings-menu.component';
import {HelpMenuComponent} from '../element/help-menu/help-menu.component';

const componentsById: {[key: string]: Type<any>} = {
  'game': GameMenuComponent,
  'session': SessionMenuComponent,
  'openings': OpeningsMenuComponent,
  'help': HelpMenuComponent,
};

@Directive({
  selector: '[cMenuBarButton]'
})
export class MenuBarButtonDirective implements OnDestroy {

  @Input('cMenuBarButton')
  menuType: string | undefined;

  @Input('align-menu')
  alignMenu: string | undefined;

  private readonly button: HTMLButtonElement;
  private menuComponent: ComponentRef<any> | undefined = undefined;
  private documentButtonDownListener = (_: MouseEvent) => this.hideMenu();
  private buttonClickListener = (_: MouseEvent) => this.showMenu();

  constructor(private readonly viewContainerRef: ViewContainerRef, button: ElementRef<HTMLButtonElement>) {
    this.button = button.nativeElement;
    this.button.addEventListener('click', this.buttonClickListener, {capture: false});
  }

  ngOnDestroy() {
    this.hideMenu();
    this.button.removeEventListener('click', this.buttonClickListener, {capture: false});
  }

  private showMenu() {
    if(this.menuType == undefined) {
      throw new RangeError('cMenuBarButton must not be empty');
    }
    const type = componentsById[this.menuType];
    if(type == undefined) {
      throw new RangeError(`Illegal value '${this.menuType}' for attribute cMenuBarButton`);
    }
    if (this.menuComponent == undefined) {
      this.menuComponent = this.viewContainerRef.createComponent(type);
      this.menuComponent.onDestroy(() => this.menuComponent = undefined);
      const element: HTMLElement = this.menuComponent.location.nativeElement;
      element.style.visibility = 'hidden';
      element.style.top = '0';
      element.style.left = '0';
      window.setTimeout(() => this.onShowMenu(element), 100);
    }
  }

  private onShowMenu(element: HTMLElement) {
    if (this.menuComponent != undefined) {
      const rect = this.button.getBoundingClientRect();
      element.style.top = `${rect.bottom + scrollY}px`;
      if (this.alignMenu == undefined || this.alignMenu === 'left') {
        element.style.left = `${rect.left + scrollX}px`;
      } else if (this.alignMenu === 'right') {
        element.style.left = `${rect.right - element.getBoundingClientRect().width + scrollX}px`;
      }
      element.style.visibility = 'visible';
      document.addEventListener('click', this.documentButtonDownListener, {capture: false});
    }
  }

  private hideMenu() {
    if (this.menuComponent != undefined) {
      this.menuComponent.destroy();
      this.menuComponent = undefined;
      document.removeEventListener('click', this.documentButtonDownListener, {capture: false});
    }
  }
}
