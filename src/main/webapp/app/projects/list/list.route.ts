import { Route } from '@angular/router';
import { ProjectListComponent } from './list.component';

export const projectListRoute: Route = {
  path: 'projects',
  component: ProjectListComponent,
  data: {
    pageTitle: 'global/menu/account/projects'
  }
};
