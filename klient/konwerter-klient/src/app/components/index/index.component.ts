import {Component, DoCheck, OnInit} from '@angular/core';
import {ApiServiceService} from '../../shared/api-service.service';
import {Observable} from 'rxjs';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.css']
})
export class IndexComponent implements OnInit, DoCheck {
  private blob: Blob;

  constructor(private apiService: ApiServiceService, private snackBar: MatSnackBar) { }
  formats: Format[] = [
    {value: 'html-0', viewValue: 'HTML'},
    {value: 'md-1', viewValue: 'MD'}
  ];
  fileInfos?: Observable<any>;
  convertButton = false;
  convertedFile: any;
  file: any;

  ngOnInit(): void {
    this.fileInfos = this.apiService.getFiles();
  }

  ngDoCheck(): void {

  }

  convert() {
    this.convertButton = true;
  }
  public convertToMd(): void {
    this.apiService.convertHTMLToMD().subscribe(
      res => {
/*        this.convertedFile = res;
        console.log(res);*/
        this.blob = new Blob([res], {type: 'application/pdf'});

        const downloadURL = window.URL.createObjectURL(res);
        const link = document.createElement('a');
        link.href = downloadURL;
        link.download = 'test.html';
        link.click();
      },
      error => {
        alert('error - get converted file');
      }
    );
  }

  openSnackBar(message: string, action: string) {
    this.snackBar.open(message, action, {
      duration: 2000,
    });
  }
}
interface Format {
  value: string;
  viewValue: string;
}
