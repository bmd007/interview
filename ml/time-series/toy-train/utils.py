import pandas as pd


def dataset_to_time_series(data, steps_from_past=1, steps_into_future=1, dropnan=True):
    """
    Frame a time series as a supervised learning dataset.
    Arguments:
        data: Sequence of observations as a list or NumPy array.
        steps_from_past: Number of lag observations as input (X).
        steps_into_future: Number of observations as output (y).
        dropnan: Boolean whether to drop rows with NaN values.
    Returns:
        Pandas DataFrame of series framed for supervised learning.
    """
    n_vars = 1  # if type(data) is list else data.shape[1]
    df = pd.DataFrame(data)
    # extract the last column which should be predicted in the future
    dfY = df.iloc[:, -1:]
    cols, names = list(), list()
    # input sequence in the past (t-n, ... t-1)
    for i in range(steps_from_past, 0, -1):
        cols.append(df.shift(i))
        names += [('gyro_yaw(t-%d)' % (i)) for j in range(n_vars)]
    # input sequence in the present (t)
    cols.append(df)
    names += [('gyro_yaw(t)') for j in range(n_vars)]
    # forecast sequence in the future (t+1, ... t+n)
    for i in range(1, steps_into_future):
        cols.append(dfY.shift(-i))
        names += [('gyro_yaw(t+%d)' % (i)) for j in range(n_vars)]
    # put it all together
    aggregate = pd.concat(cols, axis=1)
    aggregate.columns = names
    # drop rows with NaN values
    if dropnan:
        aggregate.dropna(inplace=True)
    return aggregate


def multi_variant_dataset_tot_time_series(data, steps_from_past=1, steps_into_future=1, dropnan=True):
    """
    Frame a time series as a supervised learning dataset.
    Arguments:
        data: Sequence of observations as a list or NumPy array.
        steps_from_past: Number of lag observations as input (X).
        steps_into_future: Number of observations as output (y).
        dropnan: Boolean whether or not to drop rows with NaN values.
    Returns:
        Pandas DataFrame of series framed for supervised learning.
    """
    n_vars = 1 if type(data) is list else data.shape[1]
    df = pd.DataFrame(data)
    # extract the last column which should be predicted in the future
    dfY = df.iloc[:, -1:]
    cols, names = list(), list()
    # input sequence in the past (t-n, ... t-1)
    for i in range(steps_from_past, 0, -1):
        cols.append(df.shift(i))
        names += [('var%d(t-%d)' % (j + 1, i)) for j in range(n_vars)]
    # input sequence in the present (t)
    cols.append(df)
    names += [('var%d(t)' % (j + 1)) for j in range(n_vars)]
    # forecast sequence in the future (t+1, ... t+n)
    for i in range(1, steps_into_future):
        cols.append(dfY.shift(-i))
        names += [('var%d(t+%d)' % (j + 1, i)) for j in range(n_vars - (data.shape[1] - 1))]
    # put it all together
    aggregate = pd.concat(cols, axis=1)
    aggregate.columns = names
    # drop rows with NaN values
    if dropnan:
        aggregate.dropna(inplace=True)
    return aggregate
