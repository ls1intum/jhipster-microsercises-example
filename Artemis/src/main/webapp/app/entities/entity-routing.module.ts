import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'lecture',
        data: { pageTitle: 'artemisApp.lectureLecture.home.title' },
        loadChildren: () => import('./Lecture/lecture/lecture.module').then(m => m.LectureLectureModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
