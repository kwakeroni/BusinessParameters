import { Component } from '@angular/core';
import { Group } from './group';
import { GroupController } from './groupController';
import { ParametersService } from './parameters.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent implements GroupController {
  title = 'Business Parameters Management';

  private parametersService: ParametersService;

  controller: GroupController = this;
  group: Group;
  entries = [];
  activeEntry = null;
  activeId = null;

  constructor(private $parametersService: ParametersService) {
    this.parametersService = $parametersService;
    this.group = this.parametersService.getRoot();
  }

  goto(groupName : string): void {
    this.resetActiveEntry();

    let selected = this.parametersService.getGroup(groupName);
    if (selected){
      this.group = selected;
      this.loadEntries();
    } else {
      this.group = this.parametersService.getRoot();
    }

  }

  resetActiveEntry(){
      this.activeEntry = {};
      this.activeId = null;
  }

  selectEntry(entry): void {
    for (let e of this.entries){
        if (e == entry){
          this.activeEntry = this.cloneEntry(e);
          this.activeId = this.activeEntry["id"];
          return;
        }
      }
  }

  cloneEntry(entry): Object {
    let result = {};
    this.copyEntry(entry, result);
    return result;
  }

  copyEntry(from, to){
    for(let x in from){
      to[x] = from[x];
    }
  }

  loadEntries(){
    this.entries = this.parametersService.getEntries(this.group.name);
    if (this.group.type=='basic.simple' && this.entries.length==1){
      this.selectEntry(this.entries[0]);
    }
  }

  saveActiveEntry() {
    if (this.activeId && this.activeEntry["id"] == this.activeId){
      this.parametersService.updateEntry(this.group.name, this.activeId, this.activeEntry);
    } else if (this.activeEntry["id"] == null) {
      let newEntry = this.parametersService.insertEntry(this.group.name, this.activeEntry);
      this.selectEntry(newEntry);
    }
    this.loadEntries();
  }

  getIcon(type){
    if (type=='basic.simple'){
      return 'glyphicon-align-justify';
    } else {
      return 'glyphicon-th';
    }
  }

}





