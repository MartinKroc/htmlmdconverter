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
    {value: 'md-1', viewValue: 'MD'},
    {value: 'xml-2', viewValue: 'XML'},
    {value: 'sql-3', viewValue: 'SQL'}
  ];
  fileInfos?: Observable<any>;
  convertButton = false;
  convertedFile: any;
  file: any;
  selectedFormat: string;
  r = 'Otrzymany plik';
  u = 'Wgrany plik';
  receivedFile;
  isReceiving = true;
  isGetting = false;

  ngOnInit(): void {
    this.fileInfos = this.apiService.getFiles();
  }

  ngDoCheck(): void {

  }

  convert() {
    this.convertButton = true;
  }
  public convertFile(): void {
    if (this.selectedFormat === 'html-0') {
      console.log('meee');
      this.apiService.convertMDToHTML().subscribe(
        res => {
          // console.log(res);
          this.blob = new Blob([res], {type: 'application/pdf'});

          const downloadURL = window.URL.createObjectURL(res);
          const link = document.createElement('a');
          link.href = downloadURL;
          link.download = 'converted.html';
          link.click();
          // get data from blob
          const reader = new FileReader();
          reader.onload = () => {
            this.receivedFile = reader.result;
          };
          reader.readAsText(this.blob);
        },
        error => {
          alert('error - get converted file');
        }
      );
    }
    else if (this.selectedFormat === 'md-1') {
      this.apiService.convertHTMLToMD().subscribe(
        res => {
          console.log(res);
          this.blob = new Blob([res], {type: 'application/pdf'});

          const downloadURL = window.URL.createObjectURL(res);
          const link = document.createElement('a');
          link.href = downloadURL;
          link.download = 'converted.md';
          link.click();
        },
        error => {
          alert('error - get converted file');
        }
      );
    }
    else if (this.selectedFormat === 'sql-3') {
      this.apiService.convertHTMLToSQL().subscribe(
        res => {
          console.log(res);
          this.blob = new Blob([res], {type: 'application/pdf'});

          const downloadURL = window.URL.createObjectURL(res);
          const link = document.createElement('a');
          link.href = downloadURL;
          link.download = 'converted.sql';
          link.click();
          // get data from blob
          const reader = new FileReader();
          reader.onload = () => {
            this.receivedFile = reader.result;
          };
          reader.readAsText(this.blob);
        },
        error => {
          alert('error - get converted file');
        }
      );
    }
    else if (this.selectedFormat === 'xml-2') {
      this.apiService.convertMDToXML().subscribe(
        res => {
          console.log(res);
          this.blob = new Blob([res], {type: 'application/pdf'});

          const downloadURL = window.URL.createObjectURL(res);
          const link = document.createElement('a');
          link.href = downloadURL;
          link.download = 'converted.xml';
          link.click();
        },
        error => {
          alert('error - get converted file');
        }
      );
    }
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
