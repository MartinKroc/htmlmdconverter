import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {HistoryListComponent} from './components/history-list/history-list.component';
import {IndexComponent} from './components/index/index.component';

const routes: Routes = [
  {
    path: '', component: IndexComponent
  },
  {
    path: 'history', component: HistoryListComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
