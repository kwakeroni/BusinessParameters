import { Component, Input } from '@angular/core';

@Component({
  selector: 'entry-form',
  templateUrl: './entryForm.component.html',
})

export class EntryFormComponent {

  @Input() group;
  @Input() entry;

}
