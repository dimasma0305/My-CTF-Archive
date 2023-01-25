## Issue Description

I found a bug in the video codec on Facebook, which caused users to be able to upload videos of any length in facebook story. This can be done by changing the header length in the video to be smaller than the original. For the point of concopt, I attach the following link https://github.com/dimasma0305/CTF_Solution_Writeup/blob/master/POC/MP4_Duration_Rewrite_POC.py.

## Recommendation

Changing the implementation of how videos are uploaded to be like those on Instagram and WhatsApp might be a good choice to fixing this bug.

## References

For more information on remediation steps check out reference:
- https://superuser.com/questions/1392810/changing-duration-of-mp4-hex-editor
- https://en.wikipedia.org/wiki/MPEG-4_Part_14
- https://m.facebook.com/story.php?story_fbid=pfbid02YGrDKzfZgr2zLQBQcemRgRFcMjnyLcVLrDbyfDdAX9XkJrXQtwbYkAWt8CFw36x5l&id=100081557678527&_rdr
- https://en.wikipedia.org/wiki/MPEG-4