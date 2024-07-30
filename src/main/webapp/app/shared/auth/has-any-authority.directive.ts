import { Directive, inject, Input, TemplateRef, ViewContainerRef, effect, signal, computed } from '@angular/core';

import { AccountService } from 'app/core/auth/account.service';

/**
 * @whatItDoes Conditionally includes an HTML element if current user has any
 * of the authorities passed as the `expression`.
 *
 * @howToUse
 * ```
 *     <some-element *coreHasAnyAuthority="'ROLE_ADMIN'">...</some-element>
 *
 *     <some-element *coreHasAnyAuthority="['ROLE_ADMIN', 'ROLE_USER']">...</some-element>
 * ```
 */
@Directive({
  standalone: true,
  selector: '[coreHasAnyAuthority]',
})
export default class HasAnyAuthorityDirective {
  private authorities = signal<string | string[]>([]);

  private templateRef = inject(TemplateRef<any>);
  private viewContainerRef = inject(ViewContainerRef);

  constructor() {
    const accountService = inject(AccountService);
    const currentAccount = accountService.trackCurrentAccount();
    const hasPermission = computed(() => currentAccount()?.authorities && accountService.hasAnyAuthority(this.authorities()));

    effect(() => {
      if (hasPermission()) {
        this.viewContainerRef.createEmbeddedView(this.templateRef);
      } else {
        this.viewContainerRef.clear();
      }
    });
  }

  @Input()
  set coreHasAnyAuthority(value: string | string[]) {
    this.authorities.set(value);
  }
}
