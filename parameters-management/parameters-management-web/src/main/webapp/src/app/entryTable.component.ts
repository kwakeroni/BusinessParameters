import { Component, Input } from '@angular/core';
import { GroupController } from './groupController';

@Component({
  selector: 'entry-table',
  templateUrl: './entryTable.component.html',
})

export class EntryTableComponent {

  @Input() group;
  @Input() entries;
  @Input() controller : GroupController;

}
