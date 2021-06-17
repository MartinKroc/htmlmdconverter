import { Component, OnInit } from '@angular/core';
import {ApiServiceService} from '../../shared/api-service.service';

@Component({
  selector: 'app-history-list',
  templateUrl: './history-list.component.html',
  styleUrls: ['./history-list.component.css']
})
export class HistoryListComponent implements OnInit {
  private blob: Blob;
  receivedFile;
  historyFiles;
  displayedColumns: string[] = ['name', 'size', 'lastMod', 'btn'];
  constructor(private apiService: ApiServiceService) { }

  ngOnInit(): void {
    this.historyFiles = this.apiService.getHistory();
  }

  getFileHis(filename: string) {
    this.apiService.getFileFromHistory(filename).subscribe(
      res => {
        this.blob = new Blob([res], {type: 'application/pdf'});

        const downloadURL = window.URL.createObjectURL(res);
        const link = document.createElement('a');
        link.href = downloadURL;
        link.download = filename;
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

}
