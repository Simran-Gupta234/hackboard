import { Route } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { UserDetailsComponent } from './user-details.component';

export const userDetailsRoute: Route = {
  path: 'user-details',
  component: UserDetailsComponent,
  data: {
    authorities: ['ROLE_USER'],
    pageTitle: 'user-details.title'
  },
  canActivate: [UserRouteAccessService]
};
