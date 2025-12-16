import axios from 'axios';

const API_URL = 'http://localhost:8080/api/admin';

export interface ScheduleStopRequest {
    stationId: number;
    arrivalTime: string; // HH:mm:ss
    departureTime: string; // HH:mm:ss
    sequence: number;
}

export interface CoachRequest {
    coachNo: string;
    coachType: string;
    seatCount: number;
}

export interface CreateScheduleRequest {
    trainId: number;
    departDate: string; // YYYY-MM-DD
    stops: ScheduleStopRequest[];
    coaches: CoachRequest[];
}

export const createSchedule = async (data: CreateScheduleRequest) => {
    await axios.post(`${API_URL}/schedules`, data);
};

export const deleteListing = async (listingId: number) => {
    await axios.delete(`${API_URL}/listings/${listingId}`);
};

export const checkin = async (ticketId: number, stationId: number, employeeId: number) => {
    await axios.post(`${API_URL}/checkin`, {}, { params: { ticketId, stationId, employeeId } });
};
