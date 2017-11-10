import { Component } from '@angular/core';
import { OnInit } from '@angular/core';
import { Group } from './group';
import { Entry } from './entry';
import { GroupController } from './groupController';
import { ParametersService } from './parameters.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent implements OnInit, GroupController {
  title = 'Business Parameters Management';

  private parametersService: ParametersService;

  controller: GroupController = this;
  group: Group;
  entries = [];
  activeEntry = null;
  activeId = null;

  constructor(private $parametersService: ParametersService) {
    this.parametersService = $parametersService;
  }

  ngOnInit(): void {
    this.gotoRoot();
  }

  gotoRoot(): void {
    this.parametersService.getRoot().then(root => { this.group = root; });
  }
  goto(groupName : string): void {
    this.resetActiveEntry();

    this.parametersService.getGroup(groupName).then(group => {
        if (group){
          this.group = group;
          this.loadEntries();
        } else {
          this.gotoRoot();
        }
    });

  }

  resetActiveEntry(){
      this.activeEntry = {};
      this.activeId = null;
  }

  selectEntry(entry): void {
    for (let e of this.entries){
        if (e == entry){
          this.activeEntry = e.getParameters();
          this.activeId = e.getId();
          return;
        }
      }
  }

  loadEntries(){
    this.parametersService.getEntries(this.group.name).then(entries => {
      this.entries = entries;
      if (this.group.type=='basic.simple' && this.entries.length==1){
        this.selectEntry(this.entries[0]);
      }
    });
  }

  saveActiveEntry() {
    if (this.activeId){
      this.updateActiveEntry();
    } else {
      this.insertActiveEntry();
    }
  }

  private updateActiveEntry(){
        this.parametersService.updateEntry(this.group.name, this.activeId, this.activeEntry).then(_ => {
          this.loadEntries();
        });
  }

  private insertActiveEntry(){
      let currentGroupName = this.group.name;
      let currentId = this.activeId;
      this.parametersService.insertEntry(this.group.name, this.activeEntry).then(_ => {
      this.loadEntries();
      this.resetActiveEntry();
//        if (this.isEntryActive(currentGroupName, currentId)){
//          this.refreshEntry(currentGroupName, currentId);
//        }
      });
  }

  private refreshEntry(groupName: string, id: string){
    this.parametersService.getEntry(groupName, id).then(entry => {
      if (this.isEntryActive(groupName, id)){
        this.selectEntry(entry);
      }
    });
  }

  private isEntryActive(groupName: string, id: string){
    return this.activeId == id && this.group.name == groupName;
  }

  getIcon(type){
    if (type=='basic.simple'){
      return 'glyphicon-align-justify';
    } else {
      return 'glyphicon-th';
    }
  }

}
