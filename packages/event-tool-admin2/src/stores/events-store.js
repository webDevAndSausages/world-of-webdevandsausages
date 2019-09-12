import {writable} from "svelte/store";
import * as api from "../api";

export const events = writable([]);
export const loading = writable(false);

export const ACTIONS = {
    async getAllEvents() {
        loading.set(true);
        const evt = await api.getEvents();
        loading.set(false);
        events.set(evt)
    },
    async saveUser(editedUser) {
        loading.set(true);
        await api.saveUser(editedUser);
        loading.set(false);
        events.update(draftEvents =>
            draftEvents.map(e => {
                const foundUser = e.participants.find(p => p.id === editedUser.id)
                if (foundUser) {
                    Object.assign(foundUser, editedUser)
                }
                return e;
            })
        );
    },
    async removeUser(user) {
        loading.set(true);
        await api.removeUser(user);
        loading.set(false);
        events.update(draftEvents =>
            draftEvents.map(e => {
                e.participants = e.participants.filter(p => p.id !== user.id)
                return e;
            })
        );
    }
};
