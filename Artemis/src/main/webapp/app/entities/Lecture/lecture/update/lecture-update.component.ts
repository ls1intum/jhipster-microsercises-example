import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { ILecture, Lecture } from '../lecture.model';
import { LectureService } from '../service/lecture.service';

@Component({
  selector: 'jhi-lecture-update',
  templateUrl: './lecture-update.component.html',
})
export class LectureUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    title: [],
    description: [],
    startDate: [],
    endDate: [],
  });

  constructor(protected lectureService: LectureService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ lecture }) => {
      if (lecture.id === undefined) {
        const today = dayjs().startOf('day');
        lecture.startDate = today;
        lecture.endDate = today;
      }

      this.updateForm(lecture);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const lecture = this.createFromForm();
    if (lecture.id !== undefined) {
      this.subscribeToSaveResponse(this.lectureService.update(lecture));
    } else {
      this.subscribeToSaveResponse(this.lectureService.create(lecture));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILecture>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(lecture: ILecture): void {
    this.editForm.patchValue({
      id: lecture.id,
      title: lecture.title,
      description: lecture.description,
      startDate: lecture.startDate ? lecture.startDate.format(DATE_TIME_FORMAT) : null,
      endDate: lecture.endDate ? lecture.endDate.format(DATE_TIME_FORMAT) : null,
    });
  }

  protected createFromForm(): ILecture {
    return {
      ...new Lecture(),
      id: this.editForm.get(['id'])!.value,
      title: this.editForm.get(['title'])!.value,
      description: this.editForm.get(['description'])!.value,
      startDate: this.editForm.get(['startDate'])!.value ? dayjs(this.editForm.get(['startDate'])!.value, DATE_TIME_FORMAT) : undefined,
      endDate: this.editForm.get(['endDate'])!.value ? dayjs(this.editForm.get(['endDate'])!.value, DATE_TIME_FORMAT) : undefined,
    };
  }
}
