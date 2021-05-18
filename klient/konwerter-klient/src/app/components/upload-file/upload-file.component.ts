import { Component, OnInit } from '@angular/core';
import {Observable} from 'rxjs';
import {HttpEventType, HttpResponse} from '@angular/common/http';
import {ApiServiceService} from '../../shared/api-service.service';
import { Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-upload-file',
  templateUrl: './upload-file.component.html',
  styleUrls: ['./upload-file.component.css']
})
export class UploadFileComponent implements OnInit {
  selectedFiles?: FileList;
  currentFile?: File;
  progress = 0;
  message = '';
  fileInfos?: Observable<any>;
  u = 'Wgrany plik';
  receivedFile;
  isGetting = false;
  @Output() newItemEvent = new EventEmitter<string>();
  constructor(private apiService: ApiServiceService) { }

  ngOnInit(): void {
    this.fileInfos = this.apiService.getFiles();
  }
  selectFile(event: any): void {
    this.selectedFiles = event.target.files;
  }
  upload(): void {
    this.progress = 0;

    if (this.selectedFiles) {
      const file: File | null = this.selectedFiles.item(0);

      if (file.size < 10000000) {
        this.currentFile = file;
        //console.log(this.currentFile.type);
        this.newItemEvent.emit(this.currentFile.type);
        this.apiService.uploadFile(this.currentFile).subscribe(
          (event: any) => {
            if (event.type === HttpEventType.UploadProgress) {
              this.progress = Math.round(100 * event.loaded / event.total);
            } else if (event instanceof HttpResponse) {
              this.message = event.body.message;
              this.fileInfos = this.apiService.getFiles();
            }
          },
          (err: any) => {
            console.log(err);
            this.progress = 0;

            if (err.error && err.error.message) {
              this.message = err.error.message;
            } else {
              this.message = 'Przesłano plik!';
            }

            this.currentFile = undefined;
          });
      }
      else {
        if (file.size >= 10000000) { this.message = 'Plik jest za duży (przekracza 10 MB)'; }
        if (file.type !== 'text/html' || 'text/md') { this.message = 'Plik jest w złym formacie (akceptowane rozszerzenia: .html i .md'; }
        this.selectedFiles = undefined;
        }
      }
    }
  }

