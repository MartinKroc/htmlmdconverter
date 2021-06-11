import { Component, OnInit } from '@angular/core';
import {ApiServiceService} from '../../shared/api-service.service';

@Component({
  selector: 'app-history-list',
  templateUrl: './history-list.component.html',
  styleUrls: ['./history-list.component.css']
})
export class HistoryListComponent implements OnInit {

  historyFiles;
  displayedColumns: string[] = ['name', 'size'];
  constructor(private apiService: ApiServiceService) { }

  ngOnInit(): void {
    this.historyFiles = this.apiService.getHistory();
  }

}
