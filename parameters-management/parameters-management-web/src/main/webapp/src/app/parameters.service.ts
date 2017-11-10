import { Injectable } from '@angular/core';
import { Group } from './group';
import { Entry } from './entry';
import { Headers, Http } from '@angular/http';
// import 'rxjs/add/operator/toPromise';

  const baseUrl = 'http://localhost:8080/parameters/management';  // URL to web api

@Injectable()
export class ParametersService {



    constructor(private http: Http) { }

   getRoot(): Promise<Group> {
    return this.http.get(baseUrl + '/groups')
                       .toPromise()
                       .then(response => this.rootGroup(response.json().groups.map(obj => new Group(obj)) as Group[]))
                       .catch(this.handleError);
   }

   rootGroup(groups: Group[]): Group{
    return new Group({
               name: 'root',
               type: 'none',
               subGroups: groups
             });
   }

   dump(obj: object){
     let str = "{";

     for (let p in obj){
       str += p + ": " + obj[p] + "\n";
     }
     str += "}";
     return str;
   }

   getGroup(name: string): Promise<Group> {
        return this.http.get(baseUrl + '/groups')
                           .toPromise()
                           .then(response => {
                                let groupData = response.json().groups.filter(obj => obj.name==name);
                                return (groupData.length > 0)? new Group(groupData[0]) : null;
                              })
                           .catch(this.handleError);
   }

   getEntry(groupName: string, id: string): Promise<Entry> {
       return this.http.get(baseUrl + "/groups/"+groupName+"/entries/" + id)
                       .toPromise()
                       .then(response => new Entry(response.json()))
                       .catch(this.handleError);
   }

  getEntries(groupName: string): Promise<Entry[]> {
    return this.http.get(baseUrl + "/groups/"+groupName+"/entries")
                    .toPromise()
                    .then(response => response.json().map(obj => new Entry(obj)) as Entry[])
                    .catch(this.handleError);
  }

  insertEntry(groupName: string, entry: object): Promise<void> {
    return this.http.post(baseUrl + "/groups/"+groupName+"/entries", entry)
                    .toPromise()
                    .catch(this.handleError);
  }
  updateEntry(groupName: string, id: string, entry: object): Promise<void> {
    return this.http.patch(baseUrl + "/groups/"+groupName+"/entries/" + id, entry)
                    .toPromise()
                    .catch(this.handleError);
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

  private handleError(error: any): Promise<any> {
    console.error('An error occurred', error); // for demo purposes only
    return Promise.reject(error.message || error);
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
