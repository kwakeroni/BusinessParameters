import { Injectable } from '@angular/core';
import { Group } from './group';

@Injectable()
export class ParametersService {

   getRoot(): Group {
    return ROOT;
   }

   getGroup(name: string): Group {
    return GROUPS[name];
   }

  getEntries(groupName: string): object[] {
    return ENTRIES[groupName];
  }

  insertEntry(groupName: string, entry: object): object {
      let entries:object[] = ENTRIES[groupName];
      let newEntry = this.cloneEntry(entry);

      newEntry["id"] = (''+(COUNTER++));
      entries[entries.length] = newEntry;

      return newEntry;
  }
  updateEntry(groupName: string, id: string, entry: object): void {
    let targetEntry = this.loadEntry(groupName, id);
    this.copyEntry(entry, targetEntry);
  }

  private loadEntry(groupName: string, id: string): object {
    let entries:object[] = ENTRIES[groupName];
    for (let i in entries){
      let entry:object = entries[i];

      if (entry['id'] == id){
        return entry;
      }
    }
    throw "No entry with id " + id + " in " + groupName;
  }

  private cloneEntry(entry): Object {
    let result = {};
    this.copyEntry(entry, result);
    return result;
  }

  private copyEntry(from, to){
    for(let x in from){
      to[x] = from[x];
    }
  }
}

  let COUNTER = 1;

  const ROOT = new Group({
    name: 'root',
    type: 'none',
    subGroups: [
      {name: 'simple.tv', type: 'basic.simple'},
      {name: 'mapped.tv', type: 'basic.mapped'},
      {name: 'ranged.tv', type: 'basic.ranged'}
    ]
  });

  const _simple_tv : Group = new Group({
     name: 'simple.tv',
     type: 'basic.simple',
     parameters: ['day', 'slot', 'program'],
     definition: {
      type: 'basic.simple'
     }
  });

 const _mapped_tv : Group = new Group( {
    name: 'mapped.tv',
    type: 'basic.mapped',
    parameters: ['day', 'program'],
    definition: {
      type: 'basic.mapped',
      keyParameter: 'day',
      subGroup: {
        type: 'basic.simple'
      }
    }
  });

  const GROUPS = {
    'simple.tv': _simple_tv,
    'mapped.tv': _mapped_tv
  };

  const ENTRIES = {
    'simple.tv': [
      {id: (''+(COUNTER++)), day: 'DINSDAG', slot:'8.0', program:'TestBeeld'}
    ],
    'mapped.tv': [
      {id: (''+(COUNTER++)), day: 'ZATERDAG', program: 'Samson'},
      {id: (''+(COUNTER++)), day: 'ZONDAG', program: 'Morgen Maandag'},
      {id: (''+(COUNTER++)), day: 'MAANDAG', program: 'Gisteren Zondag'}
    ]
  };
