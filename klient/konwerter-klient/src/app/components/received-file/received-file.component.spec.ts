import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReceivedFileComponent } from './received-file.component';

describe('ReceivedFileComponent', () => {
  let component: ReceivedFileComponent;
  let fixture: ComponentFixture<ReceivedFileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReceivedFileComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReceivedFileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
