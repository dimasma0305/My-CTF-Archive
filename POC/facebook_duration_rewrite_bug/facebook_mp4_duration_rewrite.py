#!/bin/env python3
'''
Reference:
https://superuser.com/questions/1392810/changing-duration-of-mp4-hex-editor
https://en.wikipedia.org/wiki/MPEG-4_Part_14
https://m.facebook.com/story.php?story_fbid=pfbid02YGrDKzfZgr2zLQBQcemRgRFcMjnyLcVLrDbyfDdAX9XkJrXQtwbYkAWt8CFw36x5l&id=100081557678527&_rdr
https://en.wikipedia.org/wiki/MPEG-4
'''
import ffmpeg
# from pprint import pprint
from struct import pack
from numpy import int64
from argparse import ArgumentParser
from os import getcwd

# args parser
args = ArgumentParser(description="Facebook MP4 Duration Rewriter")
args.add_argument("duration", type=int, help="Duration to rewite")
args.add_argument("input", type=str)
args.add_argument("output", type=str)
args = args.parse_args()

INPUTFILE = f"{getcwd()}/{args.input}"
OUTPUTFILE = f"{getcwd()}/{args.output}"
DURATION_VALUE = args.duration  # duration of the video in seconds


def print_info(outputfile, mod_dur, duration_val) -> None:
    '''
    print duration in track 1, track 2, and track 3 if exist
    '''
    print("hex: "+str(mod_dur))
    print("int: "+str(int64(duration_val)))

    print("duration track 1 :", ffmpeg.probe(outputfile)['format']['duration'])
    try:
        print("duration track 2 :", ffmpeg.probe(
            outputfile)['streams'][0]['duration'])
    except:
        print("duration track 2 not found")
    try:
        print("duration track 3 :", ffmpeg.probe(
            outputfile)['streams'][1]['duration'])
    except:
        print("duration track 3 not found")
    return None


def find_duration_address(data) -> int:
    '''
    Find the addres of duration in mvhd.
    The calculation is obtained from the sum of address_mvhd + number_byte_mvhd + 12_null_bytes
    '''
    mvhd_address = data.find(b'mvhd')
    return mvhd_address + 4 + 12


def rewrite_duration(inputfile: str, outputfile: str, duration_offset: int, input_duration) -> None:
    '''
    rewrite the duration of mp4 vidio
    '''
    with open(inputfile, 'rb') as f:
        w = f.read()
        # rewrite the duration header
        w = w[:int(duration_offset)] + input_duration + \
            w[int(duration_offset)+8:]
        with open(outputfile, 'wb') as f:
            f.write(w)
    return None


def main():
    with open(INPUTFILE, 'rb') as f:
        data = f.read()
        # address of the duration in the header
        duration_address = find_duration_address(data)

    # convert the duration to a big-endian byte string
    mod_dur = pack('>Q', DURATION_VALUE)

    rewrite_duration(INPUTFILE, OUTPUTFILE, duration_address, mod_dur)
    print_info(OUTPUTFILE, mod_dur, DURATION_VALUE)


if __name__ == '__main__':
    main()
    print("done")
