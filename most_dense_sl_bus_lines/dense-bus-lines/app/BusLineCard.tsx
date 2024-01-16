import styles from "@/app/page.module.css";
import React, {useState} from "react";
import {BusLineDto} from "@/app/types";

interface BusLineCardProps {
    line: BusLineDto;
}

const BusLineCard = ({line}: BusLineCardProps) => {
    const [isVisible, setIsVisible] = useState<boolean>(false);


    return (
        <div className={styles.card}>
            <h2>Line number: {line.number}</h2>
            <h4>Number of stops: {line.numberOfStops}</h4>
            {
                <button key={line.number} name={"showStopsButton"} onClick={event => setIsVisible(!isVisible)}> show stops </button>
            }
            {
                isVisible && line.stops.map(stop => <div key={stop.number}>{stop.name}</div>)
            }
        </div>
    )
}

export default BusLineCard;
