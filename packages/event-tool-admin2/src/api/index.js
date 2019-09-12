import {mockEvents} from "./mockEvents";

export async function removeUser(editedUser) {
    await new Promise(resolve => setTimeout(resolve, 1000));
}

export const saveUser = async editedUser => {
    await new Promise(resolve => setTimeout(resolve, 1000));
};

export const getEvents = async () => {
    await new Promise(resolve => setTimeout(resolve, 1000));
    return mockEvents;
};
