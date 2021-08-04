import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ILecture } from '../lecture.model';
import { LectureService } from '../service/lecture.service';

@Component({
  templateUrl: './lecture-delete-dialog.component.html',
})
export class LectureDeleteDialogComponent {
  lecture?: ILecture;

  constructor(protected lectureService: LectureService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.lectureService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
